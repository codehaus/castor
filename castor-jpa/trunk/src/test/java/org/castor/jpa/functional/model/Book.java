/*
 * Copyright 2009 Lukas Lang
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
package org.castor.jpa.functional.model;

/**
 * A simple book type.
 * 
 * @author lukas.lang
 * 
 */
public class Book {

    /**
     * A unique ISBN.
     */
    private long isbn;
    /**
     * The title.
     */
    private String title;
    /**
     * The entity's version.
     */
    private long version;

    /**
     * Constructor taking an isbn and a title.
     * 
     * @param isbn
     *            a unique ISBN.
     * @param title
     *            the title of the book.
     */
    public Book(long isbn, String title) {
        this.isbn = isbn;
        this.title = title;
    }

    /**
     * Default constructor.
     */
    public Book() {
        // Does nothing.
    }

    public long getIsbn() {
        return isbn;
    }

    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}