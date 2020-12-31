import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Calculator {
    public static void main(String[] args) {
        ArrayList<String> expressions = readInData(args[0]);
        System.out.println("Sum of all results with + and * at equal precedence: " 
            + calculateTotalOfExpressionWithEqualOperatorPrecedence(expressions));
        System.out.println("Sum of all results with + at higher precedence than *: " 
            + calculateTotalOfExpressionWithHigherPlusPrecedence(expressions));
    }
    private static ArrayList<String> readInData(String fileName) {
        ArrayList<String> dataRead = new ArrayList<String>();
        try {
            File inFile = new File(fileName);
            Scanner inScanner = new Scanner(inFile);
            while (inScanner.hasNext()) {
                dataRead.add(inScanner.nextLine());
            }
            inScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Exiting due to Exception");
            e.printStackTrace();
            System.exit(0);
        }
        return dataRead;
    }
    private static long calculateTotalOfExpressionWithEqualOperatorPrecedence(
            ArrayList<String> expressions) {
        long total = 0;
        for (String expression : expressions) {
            total += calculateResultFromPostFixQueue(
                makePostFixQueue(
                    makeElementQueueWithOperatorPrecedence(expression, 0)));
        }
        return total;
    }
    private static long calculateTotalOfExpressionWithHigherPlusPrecedence(
            ArrayList<String> expressions) {
        long total = 0;
        for (String expression : expressions) {
            total += calculateResultFromPostFixQueue(
                makePostFixQueue(
                    makeElementQueueWithOperatorPrecedence(expression, 1)));
        }
        return total;
    }
    private static Queue<String> makeElementQueueWithOperatorPrecedence(
            String expression, int plusPrecedence) {
        Queue<String> reformedQueue = new LinkedList<String>();
        int openBracketCount = 0;
        for (String element : expression.split("")) {
            switch (element) {
                case "(":
                    openBracketCount ++;
                    break;
                case ")":
                    openBracketCount --;
                    break;
                case "+":
                    reformedQueue.add(element + (openBracketCount * 2 + plusPrecedence));
                    break;
                case "*":
                    reformedQueue.add(element + (openBracketCount * 2 + 0));
                    break;
                case " ":
                    break;
                default:
                    reformedQueue.add(element);
            }
        }
        return reformedQueue;
    }
    private static Queue<String> makePostFixQueue(Queue<String> reformedQueue) {
        Queue<String> postFixQueue = new LinkedList<String>();
        Stack<String> stack = new Stack<String>();
        while (reformedQueue.size() > 0) {
            String currentElement = reformedQueue.remove();
            if ("0123456789".contains(currentElement)) {
                handleOperandElement(currentElement, postFixQueue);
            } else {
                handleOperatorElement(currentElement, stack, postFixQueue);
            }
        }
        while (stack.size() > 0) {
            postFixQueue.add(stack.pop().substring(0, 1));
        }
        return postFixQueue;
    }
    private static long calculateResultFromPostFixQueue(Queue<String> postFixQueue) {
        Stack<Long> stack = new Stack<Long>();
        while (postFixQueue.size() > 0) {
            String currentElement = postFixQueue.remove();
            if ("123456789".contains(currentElement)) {
                stack.push(Long.parseLong(currentElement));
            } else {
                long first = stack.pop();
                long second = stack.pop();
                if (currentElement.equals("+")) {
                    stack.push(first + second);
                } else if (currentElement.equals("*")) {
                    stack.push(first * second);
                }
            }
        }
        return stack.pop();
    }
    private static void handleOperandElement(String operandElement, Queue<String> postFixQueue) {
        postFixQueue.add(operandElement);
    }
    private static void handleOperatorElement(
            String operatorElement, Stack<String> stack, Queue<String> postFixQueue) {
        if (stack.size() == 0) {
            stack.push(operatorElement);
        } else {
            String stackTopElement = stack.pop();
            int stackTopElementPrio = 
                Integer.parseInt(stackTopElement.substring(1, stackTopElement.length()));
            int operatorElementPrio =
                Integer.parseInt(operatorElement.substring(1, operatorElement.length()));
            if (operatorElementPrio > stackTopElementPrio) {
                stack.push(stackTopElement);
                stack.push(operatorElement);
            } else if (operatorElementPrio == stackTopElementPrio) {
                postFixQueue.add(stackTopElement.substring(0, 1));
                stack.push(operatorElement);
            } else if (operatorElementPrio < stackTopElementPrio) {
                postFixQueue.add(stackTopElement.substring(0, 1));
                handleOperatorElement(operatorElement, stack, postFixQueue);
            }
        }
    }
}
