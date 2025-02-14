package com.greenstone.mes.meal.interfaces.mq;

import java.util.Arrays;

/**
 * @author wsqwork
 * @date 2025/1/23 14:49
 */
public class TestCode  implements Runnable{

    public void run() {

    }
    /**
     * 给你一个按 非递减顺序 排序的整数数组 nums，返回 每个数字的平方 组成的新数组，要求也按 非递减顺序 排序。
     * 双指针排序
     *
     * @param nums
     * @return
     */
    public int[] sortedSquares(int[] nums) {
        int right = nums.length - 1;
        int left = 0;
        int[] result = new int[nums.length];
        int write = nums.length - 1;
        while (left < right) {
            if (nums[left] * nums[left] > nums[right] * nums[right]) {
                result[write] = nums[left] * nums[left];
                left++;
            } else {
                result[write] = nums[right] * nums[right];
                right--;
            }
            write--;
        }
        return result;
    }

    /**
     * 移除元素
     *
     * @param nums
     * @param val
     * @return
     */
    public int removeElement(int[] nums, int val) {
        int pos = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != val) {
                nums[pos] = nums[i];
                pos++;
            }
        }
        return pos;
    }

    /**
     * 删除有序数组中的重复项
     *
     * @param nums
     * @return
     */
    public int removeDuplicates(int[] nums) {
        int n = nums.length;
        if (n == 0) {
            return 0;
        }
        int fast = 1, slow = 1;
        while (fast < n) {
            if (nums[fast] != nums[fast - 1]) {
                nums[slow] = nums[fast];
                ++slow;
            }
            ++fast;
        }
        return slow;
    }

    public int removeDuplicates2(int[] nums) {
        // [1,1,1,2,2,3]
        // [1,1,2,2,3]

        // [0,0,1,1,1,1,2,3,3]
        // [0,0,1,1,2,3,3]
        int n = nums.length;
        if (n == 0) {
            return 0;
        }
        int fast = 1, slow = 1, tag = 0;
        while (fast < n) {
            if (nums[fast] != nums[fast - 1]) {
                nums[slow] = nums[fast];
                ++slow;
                tag = 0;
            } else if (tag == 0) {
                nums[slow] = nums[fast];
                ++slow;
                tag = 1;
            }
            ++fast;
        }
        return slow;
    }

    public int majorityElement(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }
        Arrays.sort(nums);
        int fast = 1, n = nums.length, total = 1;
        while (fast < n) {
            if (nums[fast] != nums[fast - 1]) {
                total = 1;
            } else {
                total++;
            }
            if (total > nums.length / 2) {
                return nums[fast];
            } else {
                fast++;
            }
        }
        return nums[0];
    }

    public void rotate(int[] nums, int k) {
        int n = nums.length - 1;
        int[] result = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            if (i + k > n) {
                result[i + k - n - 1] = nums[i];
            } else {
                result[i + k] = nums[i];
            }
        }
        System.arraycopy(result, 0, nums, 0, nums.length);
    }

    // int[] nums = {7, 6, 5, 8, 3, 4, 9, 5, 7, 7, 10};
    public int maxProfit(int[] prices) {
        int n = prices.length;
        int min = 0, profit = 0;
        for (int i = 1; i < n; i++) {
            if (prices[i] > prices[i - 1] && (prices[i - 1] < min || min == 0)) {
                min = prices[i - 1];
                profit = Math.max((prices[i] - prices[i - 1]), profit);
                for (int j = i + 1; j < n; j++) {
                    if (prices[j] - min > profit) {
                        profit = Math.max(prices[j] - min, profit);
                    }
                }
            }
        }
        return profit;
    }

    public static void main(String[] args) {
        TestCode test = new TestCode();
//        int[] nums = {1, 1, 1, 2, 2, 3, 3, 4};
        int[] nums = {7, 6, 5, 6, 3, 4, 9, 5, 7, 7, 10};
        int result = test.majorityElement(nums);
        System.out.print(result);

        //  100
        //1 4 9 16 100
    }
}
