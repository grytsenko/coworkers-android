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
package grytsenko.coworkers.data;

/**
 * Thrown if error occurred during work with data in Android database.
 */
public class DataException extends RuntimeException {

    private static final long serialVersionUID = -7368186952748292754L;

    /**
     * Creates an exception.
     * 
     * @param message
     *            the description of exception.
     */
    public DataException(String message) {
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
    public DataException(String message, Throwable cause) {
        super(message, cause);
    }

}
