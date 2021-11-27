package com.epam.java2021.library.dao.impl.mysql;

import com.epam.java2021.library.dao.BookDao;
import com.epam.java2021.library.dao.impl.mysql.util.SearchSortColumn;
import com.epam.java2021.library.dao.impl.mysql.util.Transaction;
import com.epam.java2021.library.entity.impl.Author;
import com.epam.java2021.library.entity.impl.Book;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import com.epam.java2021.library.service.util.Disjoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookSuperDao implements BookDao {
    private static final Logger logger = LogManager.getLogger(BookSuperDao.class);
    private static final SearchSortColumn validColumns =
            new SearchSortColumn("title", "isbn", "year", "author");

    private Connection conn;

    public BookSuperDao() {}
    public BookSuperDao(Connection conn) {
        this.conn = conn;
    }


    @Override
    public void create(Book book) throws DaoException {
        logger.trace("Create request: book={}", book);

        Transaction tr = new Transaction(conn);

        tr.transactionWrapper( c -> {
            BookDaoImpl bookDao = new BookDaoImpl(c);
            bookDao.create(book); // updates author list also

            book.getBookStat().setId(book.getId());

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
            if (book == null) {
                return null;
            }

            List<Book> list = resolveDependencies(c, Collections.singletonList(bookDao.read(id)));
            return list.get(0);
        });
    }

    @Override
    public void update(Book book) throws DaoException {
        logger.trace("Update request: book={}", book);

        Transaction tr = new Transaction(conn);
        tr.transactionWrapper( c -> {
            BookDaoImpl dao = new BookDaoImpl(c);
            dao.update(book);

            BookStatDaoImpl bookStatDao = new BookStatDaoImpl(c);
            bookStatDao.update(book.getBookStat());

            AuthorDaoImpl authorDao = new AuthorDaoImpl(c);
            Disjoint<Author> disjoint = new Disjoint<>(authorDao.findByBookID(book.getId()), book.getAuthors());

            for (Author author: disjoint.getToDelete()) {
                dao.deleteBound(book, author);
            }

            for (Author author: disjoint.getToAdd()) {
                dao.createBound(book, author);
            }
        });
    }

    @Override
    public void delete(long id) throws DaoException {
        logger.trace("Delete request: id={}", id);

        Transaction tr = new Transaction(conn);
        tr.transactionWrapper(c -> {
                BookDaoImpl dao = new BookDaoImpl(c);
                dao.delete(id);
                // book_stat deletes by cascade
                // book_author also
        });
    }

    @Override
    public int findByPatternCount(String what, String searchBy)
            throws ServiceException, DaoException {
        logger.trace("request: what={}, searchBy={}",
                what, searchBy);

        validColumns.check(searchBy, SearchSortColumn.SEARCH);

        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper( c -> {
            BookDaoImpl dao = new BookDaoImpl(c);
            return dao.findByPatternCount(what, searchBy);
        });
    }

    /*
    @Override
    public List<Book> findBy(String what, String searchBy) throws ServiceException, DaoException {
        logger.trace("request: what={}, searchBy={}",
                what, searchBy);

        validColumns.check(searchBy, SearchSortColumn.SEARCH);

        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper( c -> {
            BookDaoImpl dao = new BookDaoImpl(c);
            return resolveDependencies(c, dao.findBy(what, searchBy));
        });
    }
    */
    @Override
    public List<Book> findByPattern(String what, String searchBy, String sortBy, int num, int page)
            throws ServiceException, DaoException {
        logger.trace("request: what={}, searchBy={}, sortBy={}, num={}, page={}",
                what, searchBy, sortBy, num, page);

        validColumns.check(searchBy, SearchSortColumn.SEARCH);
        validColumns.check(sortBy, SearchSortColumn.SORT);

        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper( c -> {
            BookDaoImpl dao = new BookDaoImpl(c);
            return resolveDependencies(c, dao.findByPattern(what, searchBy, sortBy, num, page));
        });
    }

    @Override
    public List<Book> getBooksInBooking(long id) throws DaoException {
        logger.debug("Get books in booking request: id={}", id);

        Transaction tr = new Transaction(conn);
        return tr.noTransactionWrapper(c -> {
            BookDaoImpl dao = new BookDaoImpl(c);

            return resolveDependencies(c, dao.getBooksInBooking(id));
        });
    }

    private List<Book> resolveDependencies(Connection c, List<Book> books) throws DaoException {
        if (books == null) {
            return new ArrayList<>();
        }
        AuthorDaoImpl authorDao = new AuthorDaoImpl(c);
        BookStatDaoImpl bookStatDao = new BookStatDaoImpl(c);

        for (Book b: books) {
            if (b == null) {
                continue;
            }
            b.setBookStat(bookStatDao.read(b.getId()));
            b.setAuthors(authorDao.findByBookID(b.getId()));
        }
        return books;
    }
}
