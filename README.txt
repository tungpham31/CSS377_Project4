- Note: I turn this lab in late but I am using the 5 late-day extension rule for this one so please don’t deduct my grade. If you are not aware of this rule, please refer to it here under the section “Turning In Projects”: http://none.cs.umass.edu/~dganesan/courses/spring13/syllabus.html Thank you for grading my project.
- In this project, we add a new class Book to deal with all the abstraction of a book (its number of copies, its lock, its condition variable, the request waiting for it).
- First we implement the sell method, which is easy because it does not do anything but add more copies to the book object. So first we use the global lock, get the appropriate book object, unlock. Then use its own lock, modify the book's number of copies, notify buyer waiting and then unlock.
- With the buy method, we basically do the same thing, but the part where we modify the book's information is more complicated. We add System.currentTimeMilli() to measure the time that the buyer has waited. If he has waited long enough, we release him. 
- One special thing is that we use a queue inside the book object, which will manage all the request waiting to buy this copies of this book. And the queue will be first come first serve.
- With testing, we have created 10 test cases in client code, you can use either of them by calling them from main. Right now the current TestCase using is TestCase1().


