#Design and Implementation
The design that I implemented for this assessment is as follows...

The "Forgetting Map" will be a HashMap that will contain the key provided by the user, and a seperate object containing the following...

- The 'content' passed in by the user
- A time that the element was created in the map (this can be used to assist in deciding which element to remove in the event of a tie-breaker)
- A count element that will represent the number of times the given element has been called by the find method.

I have implemented the object storing the above three elements will be an inner class of the main ForgetMap class, as this is an object will be exclusive to the ForgetMap class.

I have insured that the add method for ForgetMap is asynchronous to prevent against multiple threads trying to add content at the same time.

The find method is currently not synchronized as the method that increases the usage count within the Innerclass is synchronized and uses an AtomicLong to store the count.

The Tie-Breaker implementation that is used is that if two values have the same usage-count, then a check on the 'time element added' will be carried out to remove the oldest of the two.

---
###Notes
I was noticing errors in my Unit tests, and implemented AtomicLong in the Usage Count to try and fix this. I then realised this was due to me missing a CountDownLatch in my Unit test. However, as the implementation of the AtomicLong is still giving required results, I have left it at is.  