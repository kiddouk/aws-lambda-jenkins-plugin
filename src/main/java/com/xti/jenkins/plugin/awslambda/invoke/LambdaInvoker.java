package com.xti.jenkins.plugin.awslambda.invoke;

/*
 * #%L
 * AWS Lambda Upload Plugin
 * %%
 * Copyright (C) 2015 XT-i
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.xti.jenkins.plugin.awslambda.exception.LambdaInvokeException;
import com.xti.jenkins.plugin.awslambda.service.JenkinsLogger;
import com.xti.jenkins.plugin.awslambda.service.JsonPathParser;
import com.xti.jenkins.plugin.awslambda.service.LambdaInvokeService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LambdaInvoker {

    private LambdaInvokeService lambda;
    private JenkinsLogger logger;

    public LambdaInvoker(LambdaInvokeService lambda, JenkinsLogger logger) {
        this.lambda = lambda;
        this.logger = logger;
    }

    public LambdaInvocationResult invoke(InvokeConfig config) throws IOException, InterruptedException {
        JsonPathParser jsonPathParser = new JsonPathParser(config.getJsonParameters(), logger);
        logger.log("%nStarting lambda invocation.");
        String output = null;
        try {
            output = lambda.invokeLambdaFunction(config);
            Map<String, String> injectables = jsonPathParser.parse(output);

            return new LambdaInvocationResult(true, injectables);
        } catch (LambdaInvokeException e) {
            logger.log(e.getMessage());
            return new LambdaInvocationResult(false, new HashMap<String, String>());
        }

    }
}
