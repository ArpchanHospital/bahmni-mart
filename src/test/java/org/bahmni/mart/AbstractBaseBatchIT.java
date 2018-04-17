package org.bahmni.mart;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles(profiles = "test", resolver = SystemPropertyActiveProfileResolver.class)
public abstract class AbstractBaseBatchIT {
    @Qualifier("martJdbcTemplate")
    @Autowired
    protected JdbcTemplate martJdbcTemplate;

    @Before
    public void setUp() throws Exception {
        martJdbcTemplate.execute("DROP SCHEMA IF EXISTS PUBLIC CASCADE; CREATE SCHEMA PUBLIC;");
    }

    @After
    public void tearDown() throws Exception {
        martJdbcTemplate.execute("DROP SCHEMA IF EXISTS PUBLIC CASCADE; CREATE SCHEMA PUBLIC;");
    }
}
