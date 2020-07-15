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
    //   Returns a collection of the TimeRanges in which mandatory attendees will be free for a given 
    // meeting request as well as including when possible optional attendees
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Set<String> attendees = new HashSet<>(request.getAttendees());
    Set<String> optAttendees = new HashSet<>(request.getOptionalAttendees());
    long requestDuration = request.getDuration();


    List<TimeRange> busyTimes = findBusyPeriods(events,attendees);
    List<TimeRange> freeTime = findFreePeriods(busyTimes, busyTimes.size());
    List<TimeRange> meets = parseMeetTimes(freeTime, requestDuration);
    List<TimeRange> optBusyTimes = findBusyPeriods(events,optAttendees);
    List<TimeRange> optFreeTime = findFreePeriods(optBusyTimes, optBusyTimes.size());

    List<TimeRange> optMeets = findOptionalOverlap(meets,optFreeTime, requestDuration); 

    if (optMeets.isEmpty() && !attendees.isEmpty()) {
        return meets;
    }
    else {
        return optMeets;
    }
  }

    //  Evaluates the ranges of overlap between two lists of TimeRanges and returns a list of the results
    private List<TimeRange> findOptionalOverlap(List<TimeRange> meets, List<TimeRange> optFreeTime, long requestDuration){
        List<TimeRange> optMeets = new ArrayList<>();
        int meetsNum = meets.size();
        int optFreeTimeNum = optFreeTime.size();

        for (int i = 0; i < meetsNum; i++) {
            for (int j = 0; j < optFreeTimeNum; j++) {

                int overlapEnd = Math.min(optFreeTime.get(j).end(),meets.get(i).end());
                int overlapStart = Math.max(optFreeTime.get(j).start(),meets.get(i).start());
                
                // overlapping and the duration of overlap is adequate for a meeting 
                if (meets.get(i).overlaps(optFreeTime.get(j)) && (requestDuration <= (overlapEnd - overlapStart))){
                    optMeets.add(TimeRange.fromStartEnd(overlapStart, overlapEnd,false));
                }      
            }
        }
        return optMeets;
    }

    // Finds the periods in which attendees are involved in events, sorts them and coalesces overlapping periods. 
    // Returns the resulting list of TimeRanges
    private List<TimeRange> findBusyPeriods(Collection<Event> events, Set<String> attendees) {
        int start = TimeRange.START_OF_DAY; 
        int end = TimeRange.END_OF_DAY;
        List<TimeRange> busyTimes = new ArrayList<>();
        
        // Adds point of start of the day to aid in coalescing of busy chunks
        busyTimes.add(TimeRange.fromStartEnd(start, start, false));

        // Determines which events have attendees of interest in them and adds that time range to list
        for (Event event : events) {
            if (!Collections.disjoint(event.getAttendees(), attendees)) {
                busyTimes.add(event.getWhen());
            }
        }

        // Adds point of end of the day to aid in coalescing busy chunks 
        busyTimes.add(TimeRange.fromStartEnd(end+1, end+1,false));
        coalesceTimes(busyTimes);

        return busyTimes;
    }

    // Coalesces overlapping busy chunks by setting the start to be the earlier of 2 chunks 
    // and the end to be the later of the 2 - the separate chunks are removed from the list and 
    // the coalesced chunk is added in its place
    private void coalesceTimes(List<TimeRange> busyTimes) {
        
        Collections.sort(busyTimes, TimeRange.ORDER_BY_START);
        int busyTimesLength = busyTimes.size();

        for (int i = 1; i < busyTimesLength;) {
            TimeRange prior = busyTimes.get(i-1);
            TimeRange current = busyTimes.get(i);
            if (prior.overlaps(current)) {
                TimeRange combined = TimeRange.fromStartEnd(prior.start(), Math.max(prior.end(),current.end()),false);
                busyTimes.remove(i);
                busyTimes.remove(i-1);            
                busyTimes.add(i-1,combined);
                busyTimesLength--;
            } 
            else {
                i++;
            }
        }
    }


    // Free periods found as the difference between the end of prior busy periods and the beginning of upcoming busy periods
    // the resultant time range is added to the list freeTime
    private List<TimeRange> findFreePeriods(List<TimeRange> busyTimes, int length) {
        List<TimeRange> freeTime = new ArrayList<>();    
        for (int i = 0; i < length-1; i++){
            freeTime.add(TimeRange.fromStartEnd(busyTimes.get(i).end(),busyTimes.get(i+1).start(),false));
        }
        return freeTime;
    }

    // Meet times parsed by finding free time periods which are long enough to hold the meeting
    // resultant time ranges are added to the list meets
    private List<TimeRange> parseMeetTimes(List<TimeRange> freeTime,  long requestDuration) {
        List<TimeRange> meets = new ArrayList<>();
        int n = freeTime.size();
        for (int i =0; i<n;i++){
            TimeRange freePeriod = freeTime.get(i);
            if (requestDuration <= (long)freeTime.get(i).duration()) {
                meets.add(freePeriod);
            }
        }    
        return meets;
    }
}
