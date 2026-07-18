/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2002 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/

package org.osjava.sj.loader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith( JUnit4.class )
public class SJDataSourceTest {
  static final Properties properties = new Properties();
  private Driver driver;
  private Connection connection;
  SJDataSource sjDataSource;

  @Before
  public void setup() throws SQLException {
    // Register the mocked driver
    driver = mock( Driver.class );
    connection = mock( Connection.class );
    DriverManager.registerDriver( driver );
    when( driver.acceptsURL( anyString() ) ).thenReturn( true );
    when( driver.connect( anyString(), any() ) ).thenAnswer(
      new Answer<Connection>() {
        @Override
        public Connection answer( InvocationOnMock invocation ) {
          if ( "password".equals( ( (Properties) invocation.getArguments()[ 1 ] ).getProperty( "password" ) ) ) {
            return connection;
          } else {
            return null;
          }
        }
      }
    );
  }

  @Test
  public void getConnectionWithPlainTextPassword() throws Exception {
    sjDataSource = new SJDataSource( "driver", "url", "username", "password", properties );
    assertNotNull( sjDataSource.getConnection() );
  }

  @Test( expected = java.sql.SQLException.class )
  public void getConnectionWithInvalidPassword() throws Exception {
    sjDataSource = new SJDataSource( "driver", "url", "username", "InvalidPassword", properties );
    assertNull( sjDataSource.getConnection() );
  }

  @Test
  public void getConnectionWithEncryptedPassword() throws Exception {
    sjDataSource =
      new SJDataSource( "driver", "url", "username", "Encrypted 2be98afc86aa7f2e4bb18bd63c99dbdde", properties );
    assertNotNull( sjDataSource.getConnection() );
  }

}