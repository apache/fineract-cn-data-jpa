/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.core.data.jpa;

import org.apache.fineract.cn.core.data.jpa.local.repository.DataSourceInstance;
import org.apache.fineract.cn.core.data.jpa.local.repository.DataSourceInstanceRepository;
import org.apache.fineract.cn.lang.AutoTenantContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    TestDataJpa.TestConfiguration.class
})
@ActiveProfiles({"local"})
public class TestDataJpa {

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Autowired
  private DataSourceInstanceRepository dataSourceInstanceRepository;

  public TestDataJpa() {
    super();
  }

  @Test
  public void testLocalDataBase() {
    final DataSourceInstance dataSourceInstance = new DataSourceInstance();
    final String randomIdentifier = UUID.randomUUID().toString().replace("-", "");
    dataSourceInstance.setIdentifier(randomIdentifier);
    dataSourceInstance.setDriverClass("org.hsqldb.jdbc.JDBCDriver");
    dataSourceInstance.setJdbcUrl("jdbc:hsqldb:mem:test");
    dataSourceInstance.setUsername("SA");

    this.dataSourceInstanceRepository.save(dataSourceInstance);
    Assert.assertNotNull(this.dataSourceInstanceRepository.findOne(randomIdentifier));

    try (final AutoTenantContext autoTenantContext = new AutoTenantContext(randomIdentifier)) {
      this.dataSourceInstanceRepository.findOne(randomIdentifier);
      Assert.fail();
    } catch (final Exception ex) {
      // do nothing expected
    }
  }

  @Configuration
  @EnableJpa
  public static class TestConfiguration {
    public TestConfiguration() {
    }
  }
}
