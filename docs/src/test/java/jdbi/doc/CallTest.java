package jdbi.doc;

import java.sql.Types;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.OutParameters;
import org.jdbi.v3.postgres.PostgresDbRule;
import org.jdbi.v3.testing.JdbiRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jdbi.v3.core.locator.ClasspathSqlLocator.findSqlOnClasspath;

public class CallTest {
    @Rule
    public JdbiRule db = PostgresDbRule.rule();

    private Handle handle;

    @Before
    public void setUp() {
        handle = db.getHandle();

        handle.execute(findSqlOnClasspath("create_stored_proc_add"));
    }

    @Test
    public void testCall() {
        // tag::invokeProcedure[]
        OutParameters result = handle
                .createCall("{:sum = call add(:a, :b)}") // <1>
                .bind("a", 13) // <2>
                .bind("b", 9) // <2>
                .registerOutParameter("sum", Types.INTEGER) // <3> <4>
                .invoke(); // <5>
        // end::invokeProcedure[]

        // tag::getOutParameters[]
        int sum = result.getInt("sum");
        // end::getOutParameters[]

        assertThat(sum).isEqualTo(22);
    }

    @Test
    public void testCallConsumer() {
        // tag::invokeProcedureConsumer[]
        handle
            .createCall("{:sum = call add(:a, :b)}")
            .bind("a", 13)
            .bind("b", 9)
            .registerOutParameter("sum", Types.INTEGER)
            .invoke(result -> {
                int sum = result.getInt("sum");
            }); //
        // end::invokeProcedureConsumer[]

        // tag::getOutParameters[]
        int sum = result.getInt("sum");
        // end::getOutParameters[]

        assertThat(sum).isEqualTo(22);
    }
}
