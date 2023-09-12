/*
 * Copyright (c) 2003, Henri Yandell
 * Copyright (c) 2015-2023, Hitachi Vantara and Others
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of Simple-JNDI nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.osjava.sj.loader;

import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.Properties;

// gives us pooling
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;

/**
 * This is a wrapper for the Pooling functionality, currently provided 
 * by Jakarta DBCP2. Having the wrapper allows the dependency to be
 * optional. 
 */
public class PoolSetup {

    public static void setupConnection(String pool, String url, String username, String password, Properties properties) throws SQLException {
        ConnectionFactory connectionFactory = null;
        if( username == null || password == null ) {
            // TODO: Suck configuration in and build a Properties to replace the null below
            connectionFactory = new DriverManagerConnectionFactory(url, null );
        } else {
            connectionFactory = new DriverManagerConnectionFactory( url, username, password );
        }
        PoolableConnectionFactory f = new PoolableConnectionFactory( connectionFactory, null );
        f.setValidationQuery( properties.getProperty( "dbcpValidationQuery" ) );
        f.setDefaultReadOnly( toBoolean( properties.getProperty( "dbcpDefaultReadOnly" ), false ) );
        f.setDefaultAutoCommit( toBoolean( properties.getProperty( "dbcpDefaultAutoCommit" ), true ) );

        // we have a pool-name to setup using dbcp
        JndiPoolConfig jndiPoolConfig = new JndiPoolConfig( properties );
        GenericObjectPool connectionPool = new GenericObjectPool(f, jndiPoolConfig );

        try {
            Class.forName("org.apache.commons.dbcp2.PoolingDriver");
        } catch( ClassNotFoundException cnfe ) {
            // not too good
            System.err.println("WARNING: DBCP2 needed but not in the classpath. ");
        }
        PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
        driver.registerPool(pool, connectionPool);
    }

    public static String getUrl(String pool) {
        return "jdbc:apache:commons:dbcp:"+pool;
    }

    private static boolean toBoolean(String str, boolean def) {
        if(str == null) {
            return def;
        } else
        if("true".equals(str)) {
            return true;
        } else
        if("false".equals(str)) {
            return false;
        } else {
            throw new RuntimeException("Unable to parse as boolean: '" + str + "'");
        }
    }

}


