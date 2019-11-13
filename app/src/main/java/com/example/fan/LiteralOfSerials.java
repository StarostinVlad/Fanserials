package com.example.fan;

import java.util.ArrayList;

/**
 * Created by Star on 03.02.2018.
 */

public class LiteralOfSerials {

        private String Name;
        private ArrayList<Serial> Items;

    LiteralOfSerials(String name, ArrayList<Serial> items) {
        Name = name;
        Items = items;
    }

    public String getName() {
            return Name;
        }

        public void setName(String name) {
            this.Name = name;
        }

        ArrayList<Serial> getItems() {
            return Items;
        }

        void setItems(ArrayList<Serial> Items) {
            this.Items = Items;
        }

}
