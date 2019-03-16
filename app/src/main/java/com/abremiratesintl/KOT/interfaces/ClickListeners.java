package com.abremiratesintl.KOT.interfaces;

import com.abremiratesintl.KOT.models.BtDevice;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.TempItems;

import java.util.List;

public interface ClickListeners {
    interface CategoryItemEvents<T> {
        void onClickedEdit(T items);

        void onDeletedItem(T items);
    }

    interface ItemClick<T> {
        void onClickedItem(T item);
    }

    interface CheckoutCountClickListeners {
        void onClickedPlus(TempItems items);

        void onClickedMinus(TempItems items);

    }

    interface MarkItemListener {
        void teset(boolean makeVisible);
    }

    interface OnItemChangedListener{
        void itemChanged(List<TempItems> list);
    }

    interface BtResponseListener {
        void interacterOne(BtDevice btDevice);
    }
}
