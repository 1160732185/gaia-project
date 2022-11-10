// Copyright 2019 Baidu Inc. All rights reserved.

package web.gaia.gaiaproject;

import javax.sql.DataSource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Lists;

/**
 * fileDesc
 *
 * @author Zixiong Gan(ganzixiong01@baidu.com)
 */
@Configuration
public class DBConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        DruidDataSource druidDataSource = DruidDataSourceBuilder.create().build();
        druidDataSource.setProxyFilters(Lists.newArrayList(statFilter()));
        return druidDataSource;
    }

    public Filter statFilter(){
        StatFilter statFilter=new StatFilter();
        statFilter.setSlowSqlMillis(1000);// 执行超过此时间的为慢sql，毫秒
        statFilter.setLogSlowSql(true);// 是否打印慢日志
        statFilter.setMergeSql(true);// 是否将日志合并起来
        return statFilter;
    }
}