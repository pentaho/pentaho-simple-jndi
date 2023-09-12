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

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;
import java.util.Properties;

public class JndiPoolConfig extends GenericObjectPoolConfig {
  public JndiPoolConfig( Properties properties ) {
    this.setMaxTotal( toInt( properties.getProperty( "dbcpMaxActive" ), 8 ) );
    this.setBlockWhenExhausted( toBoolean( properties.getProperty( "dbcpWhenExhaustedAction" ), true ) );
    this.setMaxWait( toDuration( properties.getProperty( "dbcpMaxWait" ), GenericObjectPoolConfig.DEFAULT_MAX_WAIT ) );
    this.setMaxIdle( toInt( properties.getProperty( "dbcpMaxIdle" ), GenericObjectPoolConfig.DEFAULT_MAX_IDLE ) );
    this.setMinIdle( toInt( properties.getProperty( "dbcpMinIdle" ), GenericObjectPoolConfig.DEFAULT_MIN_IDLE ) );
    this.setTestOnBorrow(
      toBoolean( properties.getProperty( "dbcpTestOnBorrow" ), GenericObjectPoolConfig.DEFAULT_TEST_ON_BORROW ) );
    this.setTestOnReturn(
      toBoolean( properties.getProperty( "dbcpTestOnReturn" ), GenericObjectPoolConfig.DEFAULT_TEST_ON_RETURN ) );
    this.setTimeBetweenEvictionRuns( toDuration( properties.getProperty( "dbcpTimeBetweenEvictionRunsMillis" )
          , GenericObjectPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS ) );
    this.setNumTestsPerEvictionRun( toInt( properties.getProperty( "dbcpNumTestsPerEvictionRun" ),
      GenericObjectPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN ) );
    this.setSoftMinEvictableIdleTime( toDuration( properties.getProperty( "dbcpMinEvictableIdleTimeMillis" ),
      GenericObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_DURATION ) );
    this.setTestWhileIdle( toBoolean( properties.getProperty( "dbcpTestWhileIdle" ), GenericObjectPoolConfig.DEFAULT_TEST_WHILE_IDLE ) );
    this.setSoftMinEvictableIdleTime( toDuration( properties.getProperty( "dbcpSoftMinEvictableIdleTimeMillis" ), GenericObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_DURATION ) );
  }

  private static int toInt( String str, int def ) {
    if ( str == null ) {
      return def;
    }
    try {
      return Integer.parseInt( str );
    } catch ( NumberFormatException nfe ) {
      throw new RuntimeException( "Unable to parse as int: '" + str + "'", nfe );
    }
  }

  private static long toLong( String str, long def ) {
    if ( str == null ) {
      return def;
    }
    try {
      return Long.parseLong( str );
    } catch ( NumberFormatException nfe ) {
      throw new RuntimeException( "Unable to parse as long: '" + str + "'", nfe );
    }
  }

  private static boolean toBoolean( String str, boolean def ) {
    if ( str == null ) {
      return def;
    } else if ( "true".equals( str ) ) {
      return true;
    } else if ( "false".equals( str ) ) {
      return false;
    } else {
      throw new RuntimeException( "Unable to parse as boolean: '" + str + "'" );
    }
  }

  private static Duration toDuration( String str, Duration def ) {
    if ( str == null ) {
      return def;
    }
    try {
      return Duration.ofMillis( Long.parseLong( str ) );
    } catch ( NumberFormatException nfe ) {
      throw new RuntimeException( "Unable to parse as long: '" + str + "'", nfe );
    }
  }
}

