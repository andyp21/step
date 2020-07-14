// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.*;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // throw new UnsupportedOperationException("TODO: Implement this method.");
    
    HashSet<String> attendees = new HashSet<String>(request.getAttendees());
    ArrayList<TimeRange> busyTimes = new ArrayList<>();
    ArrayList<TimeRange> freeTime = new ArrayList<>();
    ArrayList<TimeRange> meets = new ArrayList<>();
    long requestDuration = request.getDuration();
    int start = TimeRange.START_OF_DAY; 
    int end = TimeRange.END_OF_DAY;
    TimeRange day = TimeRange.WHOLE_DAY;

    // Covering base cases of a request that is longer than the day or having no events
    if (requestDuration>=day.duration()){
        return meets;
    }
    if (events.size()==0){
        meets.add(TimeRange.WHOLE_DAY);
        return meets;
    }

    // Adds point of start of the day to aid in coalescing of busy chunks
    busyTimes.add(TimeRange.fromStartEnd(start, start, false));

    // Determines which events have attendees of interest in them and adds that time range to list
    for (Event event:events){
        if (!Collections.disjoint(event.getAttendees(),attendees)){
            busyTimes.add(event.getWhen());
        }
    }
    // Adds point of end of the day to aid in coalescing busy chunks 
    busyTimes.add(TimeRange.fromStartEnd(end+1, end+1,false));

    coalesceTimes(busyTimes);
    findFreePeriods(freeTime, busyTimes, busyTimes.size());
    parseMeetTimes(freeTime, meets, requestDuration);

    return meets;
  }
    // Coalesces overlapping busy chunks by setting the start to be the earlier of 2 chunks 
    // and the end to be the later of the 2 - the separate chunks are removed from the list and 
    // the coalesced chunk is added in its place
    public void coalesceTimes(ArrayList<TimeRange> busyTimes){
        
        Collections.sort(busyTimes,TimeRange.ORDER_BY_START);
        int busyTimesLength = busyTimes.size();

        for (int i = 1; i < busyTimesLength; i++){
            TimeRange prior = busyTimes.get(i-1);
            TimeRange current = busyTimes.get(i);
            if (prior.overlaps(current)){
                TimeRange combined = TimeRange.fromStartEnd(prior.start(), Math.max(prior.end(),current.end()),false);
                busyTimes.remove(i);
                busyTimes.remove(i-1);            
                busyTimes.add(i-1,combined);
                busyTimesLength--;i--;
            }
        }
        return;
    }

    // Free periods found as the difference between the end of prior busy periods and the beginning of upcoming busy periods
    // the resultant time range is added to the list freeTime
    public void findFreePeriods(ArrayList<TimeRange> freeTime, ArrayList<TimeRange> busyTimes,int length){
        for (int i = 0; i < length-1; i++){
            freeTime.add(TimeRange.fromStartEnd(busyTimes.get(i).end(),busyTimes.get(i+1).start(),false));
        }
    }

    // Meet times parsed by finding free time periods which are long enough to hold the meeting
    // resultant time ranges are added to the list meets
    public void parseMeetTimes(ArrayList<TimeRange> freeTime, ArrayList<TimeRange> meets,  long requestDuration){
        int n = freeTime.size();
        for (int i =0; i<n;i++){
            TimeRange freePeriod = freeTime.get(i);
            if (requestDuration <= (long)freeTime.get(i).duration()){
                meets.add(freePeriod);
            }
        }    
    }
}
