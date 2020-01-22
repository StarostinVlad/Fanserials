package com.example.fan.utils;

import com.example.fan.utils.Serial;

import java.util.ArrayList;

/**
 * Created by Star on 03.02.2018.
 */

public class LiteralOfSerials {

        private String Name;
        private ArrayList<Serial> Items;

    public LiteralOfSerials(String name, ArrayList<Serial> items) {
        Name = name;
        Items = items;
    }

    public String getName() {
            return Name;
        }

        public void setName(String name) {
            this.Name = name;
        }

        public ArrayList<Serial> getItems() {
            return Items;
        }

        void setItems(ArrayList<Serial> Items) {
            this.Items = Items;
        }

}
