/**
 * Copyright (C) 2013 Anton Grytsenko (anthony.grytsenko@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grytsenko.coworkers.sync;

/**
 * Thrown if synchronization could not be completed.
 */
public class SyncException extends RuntimeException {

    private static final long serialVersionUID = 1678007155060368790L;

    /**
     * Creates an exception.
     * 
     * @param message
     *            the description of exception.
     */
    public SyncException(String message) {
        super(message);
    }

    /**
     * Creates an exception.
     * 
     * @param message
     *            the description of exception.
     * @param cause
     *            the cause of this exception.
     */
    public SyncException(String message, Throwable cause) {
        super(message, cause);
    }

}
