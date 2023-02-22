/*
 * Copyright (c) 2017-2018 VMware, Inc. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vmware.operations;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * A validation instance that provides an asynchronous wrapper around
 * a synchronous implementation which determines validity of an aspect of
 * an operation.
 *
 * @param <T> Operation type this validator can be attached to.
 */
public abstract class ValidatorAsyncBase<T extends Operation> implements Validator {
    /**
     * Constructor.
     */
    public ValidatorAsyncBase() {
    }

    @Override
    public CompletableFuture<Void> validateExecutionAsync(ExecutorService executorService, Operation initiatingOp) {
        try {
            // ClassCastException is expected if the operation cannot be cast to the expected type
            @SuppressWarnings({"unchecked"})
            T expectedInitiatingOp = (T) initiatingOp;
            return validateExecution(executorService, expectedInitiatingOp);
        } catch (Throwable th) {
            CompletableFuture<Void> result = new CompletableFuture<>();
            result.completeExceptionally(th);
            return result;
        }

    }

    @Override
    public CompletableFuture<Void> validateRevertAsync(ExecutorService executorService, Operation initiatingOp) {
        try {
            // ClassCastException is expected if the operation cannot be cast to the expected type
            @SuppressWarnings({"unchecked"})
            T expectedInitiatingOp = (T) initiatingOp;
            return validateRevert(executorService, expectedInitiatingOp);
        } catch (Throwable th) {
            CompletableFuture<Void> result = new CompletableFuture<>();
            result.completeExceptionally(th);
            return result;
        }
    }

    /**
     * Perform validations after an execution operation.
     * @param executorService executor to use when submitting async calls
     * @param initiatingOp the operation that was reverted.
     * @return A CompletableFuture that completes when the validation has completed
     * @throws Exception if any of the validators throw
     */
    public abstract CompletableFuture<Void> validateExecution(ExecutorService executorService,
                                                              T initiatingOp) throws Exception;

    /**
     * Perform validations after a revert (not cleanup) operation.
     * @param executorService executor to use when submitting async calls
     * @param initiatingOp the operation that was reverted.
     * @return A CompletableFuture that completes when the validation has completed
     * @throws Exception if any of the validators throw
     */
    public abstract CompletableFuture<Void> validateRevert(ExecutorService executorService,
                                                           T initiatingOp) throws Exception;
}
