/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package tntrun;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

	public class MySQL {
	private static final Logger logger = Bukkit.getLogger();
	protected boolean connected = false;
	public Connection c = null;
	private String driver;
	private String connectionString;
	private TNTRun pl;

	protected static enum Statements {
		SELECT, INSERT, UPDATE, DELETE, DO, REPLACE, LOAD, HANDLER, CALL, CREATE, ALTER, DROP, TRUNCATE, RENAME, START,
		COMMIT, ROLLBACK, SAVEPOINT, LOCK, UNLOCK, PREPARE, EXECUTE, DEALLOCATE, SET, SHOW, DESCRIBE, EXPLAIN, HELP,
		USE, ANALYZE, ATTACH, BEGIN, DETACH, END, INDEXED, ON, PRAGMA, REINDEX, RELEASE, VACUUM;
	}

	public MySQL(TNTRun plugin) {
		this.pl = plugin;
	}

	public MySQL(String hostname, int port, String database, String username, String password, String useSSL, String flags, TNTRun plugin) {
		driver           = "com.mysql.jdbc.Driver";
		connectionString = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=" + useSSL + "&" + flags + "&user=" + username + "&password=" + password;
		this.pl = plugin;
	}

	public Connection open() {
		try {
			Class.forName(driver);
			this.c = DriverManager.getConnection(connectionString);
			return c;
		} catch (SQLException e) {
			System.out.println("Could not connect to Database! because: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println(driver + " not found!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return this.c;
	}

	public Connection getConn() {
		return this.c;
	}

	public void close() {
		try {
			if (c != null) {
				c.close();
			}
		} catch (SQLException ex) {
			pl.getLogger().info(ex.getMessage());
		}

		c = null;
	}

	public boolean isConnected() {
		try {
			return (((c == null) || c.isClosed())
					? false
					: true);
		} catch (SQLException e) {
			return false;
		}
	}

	public Result query(final String query) {
		if (!isConnected()) {
			open();
		}

		return query(query, true);
	}

	public Result query(final String query, boolean retry) {
		if (!isConnected()) {
			open();
		}
		try {
			PreparedStatement statement = null;

			try {
				if (!isConnected()) {
					open();
				}

				statement = c.prepareStatement(query);

				if (statement.execute()) {
					return new Result(statement, statement.getResultSet());
				}
			} catch (final SQLException e) {
				final String msg = e.getMessage();

				logger.severe("Database query error: " + msg);

				if (retry && msg.contains("_BUSY")) {
					logger.severe("Retrying query...");
					new BukkitRunnable() {
						@Override
						public void run() {
							query(query, false);
						}
					}.runTaskLater(pl, 20L);
				}
			}

			if (statement != null) {
				statement.close();
			}
		} catch (SQLException ex) {
			pl.getLogger().info(ex.getMessage());
		}

		return null;
	}

	protected Statements getStatement(String query) {
		String trimmedQuery = query.trim();

		if (trimmedQuery.substring(0, 6).equalsIgnoreCase("SELECT")) {
			return Statements.SELECT;
		}

		if (trimmedQuery.substring(0, 6).equalsIgnoreCase("INSERT")) {
			return Statements.INSERT;
		}

		if (trimmedQuery.substring(0, 6).equalsIgnoreCase("UPDATE")) {
			return Statements.UPDATE;
		}

		if (trimmedQuery.substring(0, 6).equalsIgnoreCase("DELETE")) {
			return Statements.DELETE;
		}

		if (trimmedQuery.substring(0, 6).equalsIgnoreCase("CREATE")) {
			return Statements.CREATE;
		}

		if (trimmedQuery.substring(0, 5).equalsIgnoreCase("ALTER")) {
			return Statements.ALTER;
		}

		if (trimmedQuery.substring(0, 4).equalsIgnoreCase("DROP")) {
			return Statements.DROP;
		}

		if (trimmedQuery.substring(0, 8).equalsIgnoreCase("TRUNCATE")) {
			return Statements.TRUNCATE;
		}

		if (trimmedQuery.substring(0, 6).equalsIgnoreCase("RENAME")) {
			return Statements.RENAME;
		}

		if (trimmedQuery.substring(0, 2).equalsIgnoreCase("DO")) {
			return Statements.DO;
		}

		if (trimmedQuery.substring(0, 7).equalsIgnoreCase("REPLACE")) {
			return Statements.REPLACE;
		}

		if (trimmedQuery.substring(0, 4).equalsIgnoreCase("LOAD")) {
			return Statements.LOAD;
		}

		if (trimmedQuery.substring(0, 7).equalsIgnoreCase("HANDLER")) {
			return Statements.HANDLER;
		}

		if (trimmedQuery.substring(0, 4).equalsIgnoreCase("CALL")) {
			return Statements.CALL;
		}

		return Statements.SELECT;
	}

	public class Result {
		private ResultSet resultSet;
		private Statement statement;

		public Result(Statement statement, ResultSet resultSet) {
			this.statement = statement;
			this.resultSet = resultSet;
		}

		public ResultSet getResultSet() {
			return this.resultSet;
		}

		public void close() {
			try {
				this.statement.close();
				this.resultSet.close();
			} catch (SQLException e) {}
		}
	}
}
