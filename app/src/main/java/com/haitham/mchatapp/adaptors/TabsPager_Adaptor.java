package com.haitham.mchatapp.adaptors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.haitham.mchatapp.ChatFragment;
import com.haitham.mchatapp.ContactsFragment;
import com.haitham.mchatapp.GroupsFragment;

public class TabsPager_Adaptor extends FragmentPagerAdapter {

    public TabsPager_Adaptor(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;

            }
            case 1: {
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
            }
            case 2: {
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;

            }
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "Chat";

            case 1:
                return "Contacts";

            case 2:
                return "Groups";

            default:
                return null;
        }
    }
}
