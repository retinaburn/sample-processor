/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.moynes.nifi.processors.sample;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyProcessorTest {

    private TestRunner testRunner;

    @BeforeEach
    public void init() {
        testRunner = TestRunners.newTestRunner(MyProcessor.class);
    }

    @Test
    public void testProcessor() {
        testRunner.setProperty(MyProcessor.MY_PROPERTY, "blah");
        testRunner.run();
        testRunner.assertQueueEmpty();

        testRunner.enqueue("DATA");
        testRunner.assertQueueNotEmpty();
        testRunner.run();

        List<MockFlowFile> results = testRunner.getFlowFilesForRelationship(MyProcessor.REL_SUCCESS);
        testRunner.assertQueueEmpty();
        assertTrue(results.size() == 1, "Flow files in success queue should be 1");
        testRunner.clearTransferState();

        MockFlowFile mockFlowFile = new MockFlowFile(12345l);
        mockFlowFile.setData("HI THERE".getBytes());
        FlowFile flowFile = mockFlowFile;
        testRunner.enqueue(flowFile);
        testRunner.run();

        log.debug("Provenance: {}", testRunner.getProvenanceEvents());

        results = testRunner.getFlowFilesForRelationship(MyProcessor.REL_SUCCESS);
        testRunner.assertQueueEmpty();
        assertTrue(results.size() == 1, "Flow files in success queue should be 1");
    }

}
