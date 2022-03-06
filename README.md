#Design

The current plan for my implementation of this assessment is the following...

The "Forgetting Map" will be a HashMap that will contain the key provided by the user, and a seperate object that will contain the following information

- The 'content' passed in by the user
- A time that the element was created in the map (this can be used to assist in deciding which element to remove in the event of a tie-breaker)
- A count element that will represent the number of times the given element has been called by the find method.

This will be an inner class as this is object will be exclusive to the Forgetting Map class.

#Plan

I have created skeleton classes that return a null class, and will use these to implement the tests that will confirm the desired outcome of the assessment.

---
###Notes

I was noticing errors in my Unit tests, and implemented AtomicLong in in the Usage Count to try and fix this. I then realised this was due to me missing a CountDownLatch in my Unit test. My code could be reverted to reverse the use of an AtomicLong, but have left that in as it is another way of handling asynchronous calls.   
