package com.example.fan;

import java.util.ArrayList;

/**
 * Created by Star on 03.02.2018.
 */

public class MyGroup {

        private String Name;
        private ArrayList<Child> Items;

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            this.Name = name;
        }

        public ArrayList<Child> getItems() {
            return Items;
        }

        public void setItems(ArrayList<Child> Items) {
            this.Items = Items;
        }

}
