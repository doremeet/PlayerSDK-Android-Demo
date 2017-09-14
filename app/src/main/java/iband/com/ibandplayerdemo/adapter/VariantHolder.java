package iband.com.ibandplayerdemo.adapter;

import com.iband.playersdk.IBandPlayerVariant;

/**
 * Created by yossibarel on 17/08/2017.
 */

public class VariantHolder {
    private final IBandPlayerVariant mVariant;
    private final int mIndex;

    public VariantHolder(int index, IBandPlayerVariant variant) {
        mIndex = index;
        mVariant = variant;

    }

    public String getLabel() {
        if (mVariant == null)
            return "Auto";
        else
            return mVariant.getHeight() + "p";

    }

    public int getIndex() {
        return mIndex;
    }

    public int getHeight() {
        return mVariant.getHeight();
    }

    public boolean isAuto() {
        return mVariant == null;
    }
}
