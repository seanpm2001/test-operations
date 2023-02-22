/*
 * Copyright (c) 2015-2018 VMware, Inc. All Rights Reserved.
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
public abstract class ValidatorSyncBase<T> implements Validator {

    /**
     * Constructor.
     */
    public ValidatorSyncBase() {
    }

    @Override
    public CompletableFuture<Void> validateExecutionAsync(ExecutorService executorService, Operation initiatingOp) {
        CompletableFuture<Void> executeResult = new CompletableFuture<>();
        try {
            executorService.execute(() -> {
                try {
                    // ClassCastException is expected if the operation cannot be cast to the expected type
                    @SuppressWarnings({"unchecked"})
                    T expectedInitiatingOp = (T) initiatingOp;
                    validateExecution(expectedInitiatingOp);
                    executeResult.complete(null);
                } catch (Throwable th) {
                    executeResult.completeExceptionally(th);
                }
            });
        } catch (Throwable th) {
            executeResult.completeExceptionally(th);
        }

        return executeResult;
    }

    @Override
    public CompletableFuture<Void> validateRevertAsync(ExecutorService executorService, Operation initiatingOp) {
        CompletableFuture<Void> executeResult = new CompletableFuture<>();
        try {
            executorService.execute(() -> {
                try {
                    // ClassCastException is expected if the operation cannot be cast to the expected type
                    @SuppressWarnings({"unchecked"})
                    T expectedInitiatingOp = (T) initiatingOp;
                    validateRevert(expectedInitiatingOp);
                    executeResult.complete(null);
                } catch (Throwable th) {
                    executeResult.completeExceptionally(th);
                }
            });
        } catch (Throwable th) {
            executeResult.completeExceptionally(th);
        }

        return executeResult;
    }

    @Override
    public CompletableFuture<Void> validateCleanupAsync(ExecutorService executorService, Operation initiatingOp) {
        CompletableFuture<Void> executeResult = new CompletableFuture<>();
        try {
            executorService.execute(() -> {
                try {
                    // ClassCastException is expected if the operation cannot be cast to the expected type
                    @SuppressWarnings({"unchecked"})
                    T expectedInitiatingOp = (T) initiatingOp;
                    validateCleanup(expectedInitiatingOp);
                    executeResult.complete(null);
                } catch (Throwable th) {
                    executeResult.completeExceptionally(th);
                }
            });
        } catch (Throwable th) {
            executeResult.completeExceptionally(th);
        }

        return executeResult;
    }

    /**
     * Perform validations after an execution operation.
     * @param initiatingOp the operation that was executed.
     * @throws Exception if the validators throw
     */
    public abstract void validateExecution(T initiatingOp) throws Exception;

    /**
     * Perform validations after a revert (not cleanup) operation.
     * @param initiatingOp the operation that was reverted.
     * @throws Exception if the validators throw
     */
    public abstract void validateRevert(T initiatingOp) throws Exception;

    /**
     * Perform validations after a cleanup operation.
     * @param initiatingOp the operation that was cleaned up.
     * @throws Exception if the validators throw
     */
    public void validateCleanup(T initiatingOp) throws Exception {}
}
