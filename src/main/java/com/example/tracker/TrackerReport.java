package com.example.tracker;

import java.util.Map;
import com.example.model.Stats;

public interface TrackerReport {

    Map<String, Stats> getResult();

}
