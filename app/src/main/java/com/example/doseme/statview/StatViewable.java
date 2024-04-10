package com.example.doseme.statview;

import java.util.ArrayList;

public interface StatViewable {
    int get_nr_dsets();
    double get_val(int pos);

    String get_label(int pos);

    String get_alt_label(int pos);

    char get_min_label(int pos);
    double get_maxval();

    ArrayList<Integer> get_highlight_idx();

}
