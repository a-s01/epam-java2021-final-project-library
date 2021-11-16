package com.epam.java2021.library.dao.daoImpl.mysql;

import com.epam.java2021.library.dao.BookDao;
import com.epam.java2021.library.service.DependencyHolder;
import com.epam.java2021.library.entity.entityImpl.Book;
import com.epam.java2021.library.entity.entityImpl.EditRecord;
import com.epam.java2021.library.exception.DaoException;
import com.epam.java2021.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.*;

public class BookDaoImpl implements BookDao {
    private static final Logger logger = LogManager.getLogger(BookDaoImpl.class);
    private static final Set<String> COLUMNS = new HashSet<>();
    private final DependencyHolder<Book> holder = new DependencyHolder<>();

    static {
        COLUMNS.add("title");
        COLUMNS.add("isbn");
        COLUMNS.add("year");
        COLUMNS.add("author");
    }

    private DaoImpl<Book> daoImpl;
    private Connection conn;

    private void checkDaoImpl() throws ServiceException {
        if (daoImpl == null) {
            throw new ServiceException("You should call 'setConnection' function first");
        }
    }
    @Override
    public void create(Book book) throws DaoException, ServiceException {
        checkDaoImpl();
        final String query = "INSERT INTO book VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";
        daoImpl.create(book, query, this::fillStatement);
    }

    @Override
    public Book read(long id) throws DaoException, ServiceException {
        checkDaoImpl();
        final String query = "SELECT * FROM book WHERE id = ?";
        return daoImpl.read(id, query, this::parse);
    }

    @Override
    public void update(Book book) throws DaoException, ServiceException {
        checkDaoImpl();

        final String query = "UPDATE book SET title = ?, isbn = ?, year = ?, lang_id = ?, keep_period = ?, last_edit_id = ?";
        daoImpl.update(book, query, this::fillStatement);
    }

    @Override
    public void delete(Book book) throws DaoException, ServiceException {
        checkDaoImpl();

        final String query = "DELETE * FROM book WHERE id = ?";
        daoImpl.delete(book, query);
    }

    @Override
    public void setConnection(Connection conn) {
        this.conn = conn;
        daoImpl = new DaoImpl<>(conn, logger, "book");
    }

    @Override
    public List<Book> findByPattern(String what, String searchBy, String sortBy) throws ServiceException, DaoException {
        checkDaoImpl();

        logger.trace("findbyPattern request: what={}, searchBy={}, sortBy={}", what, searchBy, sortBy);
        final String query = "SELECT * FROM book WHERE %s LIKE ? ORDER BY %s";
        if (!COLUMNS.contains(searchBy)) {
            throw new ServiceException("Column " + searchBy + " isn't supported for search operation");
        }
        if (!COLUMNS.contains(sortBy)) {
            throw new ServiceException("Column " + sortBy + " isn't supported for sort operation");
        }
        if (searchBy.equals("author")) {
            return findByAuthorNamePattern(what, sortBy);
        }

        return daoImpl.findByPattern(what, String.format(query, searchBy, sortBy), this::parse);
    }

    private List<Book> findByAuthorNamePattern(String pattern, String sortBy) throws DaoException {
        String orderBy;
        if (sortBy.equals("author")) {
            orderBy = "a.name";
        } else {
            orderBy = "b." + sortBy;
        }

        final String query = "SELECT *\n" +
                "  FROM book AS b\n" +
                "   JOIN book_author AS ba\n" +
                "      ON b.id=ba.book_id\n" +
                "      JOIN author AS a\n" +
                "        ON a.id = ba.author_id\n" +
                "     WHERE a.name LIKE ?" +
                "    ORDER BY " + orderBy;
        return daoImpl.findByPattern(pattern, query, this::parse);
    }

    @Override
    public DependencyHolder<Book> getDependencies() {
        return holder;
    }

    private Book parse(ResultSet rs) throws SQLException, DaoException {
        Book.Builder builder = new Book.Builder();
        builder.setId(rs.getInt("id"));
        builder.setTitle(rs.getString("title"));
        builder.setIsbn(rs.getString("ISBN"));
        builder.setKeepPeriod(rs.getInt("keep_period"));
        builder.setCreated(rs.getDate("created"));
        builder.setYear(rs.getDate("year"));
        Book book = builder.build();

        long langID = rs.getInt("lang_id");
        long lastEditID = rs.getInt("last_edit_id");
        holder.set(book, "landID", langID);
        holder.set(book, "lastEditID", lastEditID);

        return book;
    }

    private void fillStatement(Book book, PreparedStatement ps) throws SQLException {
        int i = daoImpl.START;
        ps.setString(i++, book.getTitle());
        ps.setString(i++, book.getIsbn());
        ps.setLong(i++, book.getLang().getId());
        ps.setInt(i++, book.getKeepPeriod());
        EditRecord lastEdit = book.getLastEdit();
        if (lastEdit != null) {
            ps.setLong(i++, lastEdit.getId());
        } else {
            ps.setNull(i++, Types.INTEGER);
        }
    }

    @Override
    public void setLastEdit(Book book, EditRecord lastEdit) throws DaoException, ServiceException {
        checkDaoImpl();

        final String query = "UPDATE book SET last_edit_id = ? WHERE id = ?";
        book.setLastEdit(lastEdit);
        daoImpl.updateLongField(lastEdit.getId(), book, query);
    }
}
