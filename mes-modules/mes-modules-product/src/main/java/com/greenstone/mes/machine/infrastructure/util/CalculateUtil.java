package com.greenstone.mes.machine.infrastructure.util;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wushaoqi
 * @date 2024-02-27-15:15
 */
public class CalculateUtil {

    public static double evaluate(String expression) {
        Evaluator evaluator = new Evaluator();
        try {
            return evaluator.getNumberResult(expression);
        } catch (EvaluationException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double evaluate(Map<String, String> params, String expression) {
        Evaluator evaluator = new Evaluator();
        try {
            evaluator.setVariables(params);
            System.out.println(evaluator.replaceVariables(expression));
            return evaluator.getNumberResult(expression);
        } catch (EvaluationException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) {
        System.out.println("计算结果：" + evaluate("11+22*2+10/2-8"));
        Map<String, String> params2 = new HashMap<>();
        params2.put("a", "11");
        params2.put("b", "22");
        params2.put("c", "2");
        params2.put("d", "10");
        params2.put("e", "2");
        params2.put("f", "8");
        System.out.println("变量计算结果：" + evaluate(params2, "#{a}+#{b}*#{c}+#{d}/#{e}-#{f}"));
    }
}
