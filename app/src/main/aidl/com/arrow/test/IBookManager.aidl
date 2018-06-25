// IBookManager.aidl
package com.arrow.test;

// Declare any non-default types here with import statements
import com.arrow.test.Book;

interface IBookManager {
    List<Book> getBookList();
    void addBookList(in Book book);
}
