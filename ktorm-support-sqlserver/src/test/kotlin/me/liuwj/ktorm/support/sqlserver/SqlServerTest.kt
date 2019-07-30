package me.liuwj.ktorm.support.sqlserver

import me.liuwj.ktorm.BaseTest
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.logging.ConsoleLogger
import me.liuwj.ktorm.logging.LogLevel
import org.junit.Ignore
import org.junit.Test
import org.testcontainers.containers.MSSQLServerContainer

@Ignore
class SqlServerTest : BaseTest() {

    class KMSSQLServerContainer: MSSQLServerContainer<KMSSQLServerContainer>()

    private val MSSQLServer = KMSSQLServerContainer()

    override fun init() {
        MSSQLServer.start()

        Database.connect(
            url = MSSQLServer.jdbcUrl,
            driver = MSSQLServer.driverClassName,
            user = MSSQLServer.username,
            password = MSSQLServer.password,
            logger = ConsoleLogger(threshold = LogLevel.TRACE)
        )

        execSqlScript("init-sqlserver-data.sql")
    }

    @Test
    fun testPagingSql() {
        var query = Employees
            .leftJoin(Departments, on = Employees.departmentId eq Departments.id)
            .select(Employees.id, Employees.name)
            .orderBy(Employees.id.desc())
            .limit(0, 1)

        assert(query.totalRecords == 4)
        assert(query.count() == 1)

        query = Employees
            .selectDistinct(Employees.departmentId)
            .limit(0, 1)

        assert(query.totalRecords == 2)
        assert(query.count() == 1)

        query = Employees
            .select(Employees.name)
            .limit(0, 1)

        assert(query.totalRecords == 4)
        assert(query.count() == 1)
    }
}
