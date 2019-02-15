package algorithms;

import interfaces.AbstractAlgorithmInterface;
import Parser.DataRecord;

public abstract class BinarySearcher implements AbstractAlgorithmInterface {
    /**
     *
     * uses binary search to find the optimal height for the rectangles
     *
     * @param record {@link DataRecord}
     */
    @Override
    public void solve(DataRecord record) {
        //float height = 1f;
        //System.out.println(isSolvable(record, height));
        //getSolution(record, height);

        float alpha = record.aspectRatio;
        // --------- calculating the initial bounds -------------
        // estimate of upper bound which may be too low
        int high = (int)(10000 * Math.sqrt(alpha / record.points.size()));
        int low = 0;


        // make sure that our upper bound is correct. This may not be required if we have a good estimation
        while (isSolvable(record, high)) {
            low = high;
            high = (int) (high * 1.5);
            // TODO: this could run into an infinite loop if everything is solvable
        }
        // ----------- execute binary search ---------
        // first binary search to reduce to integral values (width)
        while (low < high - 1) {
            int mid = (high + low) / 2;
            if (isSolvable(record, mid)) {
                low = mid;
            } else {
                high = mid;
            }
        }

        float height = low;
        int i_low, i_high;

        // check half step of width and calculate indices for height search
        if (isSolvable(record, low + 0.5f)) {
            // System.out.println("so far: " + (low + 0.5) + " " + high);
            i_low =  (int) Math.ceil((low + 0.5) / alpha);
            i_high =  (int) Math.ceil((high) / alpha);
            height = low + 0.5f;
        } else {
            // System.out.println("so far: " + (low) + " " + (low + 0.5));
            i_low =  (int) Math.ceil((low) / alpha);
            i_high =  (int) Math.ceil((low + 0.5) / alpha);
        }

        // binary search on height
        while (i_low < i_high - 1) {
            int mid = (i_high + i_low) / 2;
            if (isSolvable(record, mid * alpha)) {
                i_low = mid;
            } else {
                i_high = mid;
            }
        }

        // check if new value is valid
        if(i_low * alpha < high && i_low * alpha > low) {
            if (isSolvable(record, i_low * alpha)) {
                height = Math.max(low, i_low * alpha);
            }
        }


        // -------- execute algorithm ---------
        System.out.println(height);
        getSolution(record, height);
    }

    /**
     * returns if labels can be placed for a given height
     *
     * @modifies none
     * @param record {@link DataRecord}
     * @param height required height for rectangles
     */
    abstract boolean isSolvable(DataRecord record, float height);

    /**
     * Place all labels with the given height (this method is not robust to reduce computation time)
     *
     * @modifies nodes
     * @param record {@link DataRecord}
     * @param height required height for rectangles
     * @pre isSolvable(nodes, height)
     */
    abstract void getSolution(DataRecord record, float height);

}