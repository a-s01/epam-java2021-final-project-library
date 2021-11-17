package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.BookDao;
import com.epam.java2021.library.dao.SuperDao;
import com.epam.java2021.library.dao.impl.util.Transaction;
import com.epam.java2021.library.entity.impl.Author;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.entity.impl.BookStat;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO update book-author list
public class BookSuperDao implements SuperDao<Book> {
    private static final Logger logger = LogManager.getLogger(BookSuperDao.class);

    public static class SearchSortColumns {
        private SearchSortColumns() {}
        private static final Set<String> COLUMNS = new HashSet<>();
        static {
            COLUMNS.add("title");
            COLUMNS.add("isbn");
            COLUMNS.add("year");
            COLUMNS.add("author");
        }
        public static void check(String s, String action) throws ServiceException {
            if (!COLUMNS.contains(s)) {
                logger.error("{} forbidden for column {}", action, s);
                throw new ServiceException(action + " forbidden for column " + s);
            }
        }
    }

    private Connection conn;


    public BookSuperDao() {}
    public BookSuperDao(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void create(Book book) throws DaoException {
        logger.trace("Create request: book={}", book);
        final String bookAuthorBound = "INSERT INTO book_author VALUES (?, ?)";

        Transaction tr = new Transaction(conn);
        tr.transactionWrapper( c -> {
            BookDaoImpl bookDao = new BookDaoImpl(c);
            bookDao.create(book);

            DaoImpl<Book> daoImpl = new DaoImpl<>(c, logger);
            // authors should be already in DB
            for (Author author: book.getAuthors()) {
                daoImpl.updateLongField(book.getId(), author.getId(), bookAuthorBound);
            }
            BookStatDaoImpl bookStatDao = new BookStatDaoImpl(c);
            bookStatDao.create(book.getBookStat());
            // TODO add editing history
        });
    }

    @Override
    public Book read(long id) throws DaoException {
        logger.trace("Read request: id={}", id);

        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper(c -> {
                BookDaoImpl bookDao = new BookDaoImpl(c);
                Book book = bookDao.read(id);

                BookStatDaoImpl bookStatDao = new BookStatDaoImpl(c);
                BookStat bookStat = bookStatDao.read(book.getId());
                book.setBookStat(bookStat);

                AuthorDaoImpl authorDao = new AuthorDaoImpl(c);
                List<Author> authors = authorDao.findByBookID(book.getId());
                book.setAuthors(authors);
                return book;
        });
    }

    // Don't update author list, do it separately
    @Override
    public void update(Book book) throws DaoException {
        logger.trace("Update request: book={}", book);

        Transaction tr = new Transaction(conn);
        tr.transactionWrapper( c -> {
                    BookDaoImpl dao = new BookDaoImpl(c);
                    dao.update(book);

                    BookStatDaoImpl bookStatDao = new BookStatDaoImpl(c);
                    bookStatDao.update(book.getBookStat());
        });
    }

    @Override
    public void delete(Book book) throws DaoException {
        logger.trace("Delete request: book={}", book);

        Transaction tr = new Transaction(conn);
        tr.transactionWrapper(c -> {
                BookDaoImpl dao = new BookDaoImpl(c);
                dao.delete(book);
                // book_stat deletes by cascade
                // book_author also
        });
    }

    @Override
    public List<Book> findByPattern(String what, String searchBy, String sortBy, int num, int page)
            throws ServiceException, DaoException {
        logger.trace("Find by pattern request: what={}, searchBy={}, sortBy={}, num={}, page={}",
                what, searchBy, sortBy, num, page);

        SearchSortColumns.check(searchBy, "Search");
        SearchSortColumns.check(sortBy, "Sort");

        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapperList( c -> {
            BookDaoImpl dao = new BookDaoImpl(c);
            List<Book> books;
            books = dao.findByPattern(what, searchBy, sortBy, num, page);

            AuthorDaoImpl authorDao = new AuthorDaoImpl(c);
            BookStatDaoImpl bookStatDao = new BookStatDaoImpl(c);
            for (Book b: books) {
                b.setBookStat(bookStatDao.read(b.getId()));
                b.setAuthors(authorDao.findByBookID(b.getId()));
            }
            return books;
        });
    }
}
