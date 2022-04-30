import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLQueryServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private Statement statement;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			Class.forName(config.getInitParameter("databaseDriver"));
			connection = DriverManager.getConnection(config.getInitParameter("databaseName"), config.getInitParameter("username"), config.getInitParameter("password"));
			statement = connection.createStatement();
		}

		catch (Exception e) {
			e.printStackTrace();
			throw new UnavailableException(e.getMessage());
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String textBox = request.getParameter("textBox");
		String textBoxLC = textBox.toLowerCase();
		String result = null;
		
		if (textBoxLC.contains("select")) {

			try {
				result = doSelectQuery(textBoxLC);
			} catch (SQLException e) {
				result = "<span>" + e.getMessage() + "</span>";

				e.printStackTrace();
			}
		}
		else {
			try {
				result = doUpdateQuery(textBoxLC);
			}catch(SQLException e) {
				result = "<span>" + e.getMessage() + "</span>";

				e.printStackTrace();
			}
		}

		HttpSession session = request.getSession();
		session.setAttribute("result", result);
		session.setAttribute("textBox", textBox);
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public String doSelectQuery(String textBox) throws SQLException {
		String result;
		ResultSet table = statement.executeQuery(textBox);

		ResultSetMetaData metaData = table.getMetaData();
		int numCol = metaData.getColumnCount();
		String tableOpeningHTML = "<div class='container-fluid'><div class='row justify-content-center'><div class='table-responsive-sm-10 table-responsive-md-10 table-responsive-lg-10'><table class='table'>";
		String tableColumnsHTML = "<thead class='thead-dark'><tr>";
		for (int i = 1; i <= numCol; i++) {
			tableColumnsHTML += "<th scope='col'>" + metaData.getColumnName(i) + "</th>";
		}

		tableColumnsHTML += "</tr></thead>";

		String tableBodyHTML = "<tbody>";
		while (table.next()) {
			tableBodyHTML += "<tr>";
			for (int i = 1; i <= numCol; i++) {
				if (i == 1)
					tableBodyHTML += "<td scope'row'>" + table.getString(i) + "</th>";
				else
					tableBodyHTML += "<td>" + table.getString(i) + "</th>";
			}
			tableBodyHTML += "</tr>";
		}

		tableBodyHTML += "</tbody>";

		String tableClosingHTML = "</table></div></div></div>";
		result = tableOpeningHTML + tableColumnsHTML + tableBodyHTML + tableClosingHTML;

		return result;
	}
	
	private String doUpdateQuery(String textBoxLC) throws SQLException {
		String result = null;
		int numRowsUpdated = 0;
		
		ResultSet preQualCheck = statement.executeQuery("select COUNT(*) from shipments where quantity >= 100");
		preQualCheck.next();
		int ShipsGreaterThan100Pre = preQualCheck.getInt(1);
		
		statement.executeUpdate("create table shipmentsBeforeUpdate like shipments");
		statement.executeUpdate("insert into shipmentsBeforeUpdate select * from shipments");
		
		numRowsUpdated = statement.executeUpdate(textBoxLC);
		result = "<div> The statement executed succesfully.<div>";

		ResultSet afterQuantityCheck = statement.executeQuery("select COUNT(*) from shipments where quantity >= 100");
		afterQuantityCheck.next();
		int ShipsGreaterThan100Post = afterQuantityCheck.getInt(1);
		if(ShipsGreaterThan100Pre < ShipsGreaterThan100Post) {
			int numRowsAffectedAfter5s = statement.executeUpdate("update suppliers set status = status + 5 where snum in ( select distinct snum from shipments left join shipmentsBeforeUpdate using (snum, pnum, jnum, quantity) where shipmentsBeforeUpdate.snum is null)");
			result += "<div>Business Logic Detected! - Updating Supplier Status</div>";
		}
		
		statement.executeUpdate("drop table shipmentsBeforeUpdate");
		
		return result;
	}
}