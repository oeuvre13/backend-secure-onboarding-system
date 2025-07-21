package com.reg.regis.test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import com.reg.regis.service.SimpleUnitTest;
import com.reg.regis.service.RegistrationServiceTest;
import com.reg.regis.service.VerificationServiceTest;

/**
 * Test Suite untuk menjalankan semua unit test sekaligus
 * 
 * Cara menjalankan:
 * 1. Via IDE: Run TestRunner class
 * 2. Via Maven: mvn test -Dtest=TestRunner
 * 3. Via Maven dengan JaCoCo: mvn clean test jacoco:report
 */
@Suite
@SuiteDisplayName("Customer Registration Service - Complete Unit Test Suite")
@SelectClasses({
    SimpleUnitTest.class,
    RegistrationServiceTest.class,
    VerificationServiceTest.class
})
public class TestRunner {
    // Test suite akan otomatis menjalankan semua test classes yang dipilih
}