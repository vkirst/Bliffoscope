The algorithm is straightforward.

I compare target object with the data from the radar at each coordinate. I count the number of unmatched pixels and then I make suggestion, that the area with minimum number of unmatched pixels is our target.


At the implementation I made the following assumptions:

1. objects don't cross the borders of radar data
2.  there is no collision between objects

