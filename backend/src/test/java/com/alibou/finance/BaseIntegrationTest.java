package com.alibou.finance;


import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseIntegrationTest extends AbstractH2IntegrationTest{
}
