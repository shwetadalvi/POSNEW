package com.abremiratesintl.KOT.interfaces;

import android.view.View;

import com.abremiratesintl.KOT.models.BtDevice;
import com.abremiratesintl.KOT.models.Items;

import java.util.List;

public interface ClickListeners {
    interface CategoryItemEvents<T> {
        void onClickedEdit(T items);

        void onDeletedItem(T items);
    }

    interface ItemClick<T> {
        void onClickedItem(T item);
    }

    interface ItemClickWithView<T>{
        void onClickedItem(View view,T item);
    }
    interface CheckoutCountClickListeners {
        void onClickedPlus(Items items);

        void onClickedMinus(Items items);

    }

    interface MarkItemListener {
        void teset(boolean makeVisible);
    }

    interface OnItemChangedListener{
        void itemChanged(List<Items> list);
    }

    interface BtResponseListener {
        void interacterOne(BtDevice btDevice);
    }
}
