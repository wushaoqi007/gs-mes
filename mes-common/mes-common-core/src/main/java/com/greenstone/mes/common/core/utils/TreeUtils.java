package com.greenstone.mes.common.core.utils;

import com.greenstone.mes.common.core.annotation.TreeChildren;
import com.greenstone.mes.common.core.annotation.TreeId;
import com.greenstone.mes.common.core.annotation.TreeParentId;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class TreeUtils {


    public static <T> List<T> toTree(List<T> nodes) {
        return toTree(nodes, null);
    }

    public static <T> List<T> toTree(List<T> nodes, Object rootId) {
        return toTree(nodes, rootId, null);
    }

    public static <T> List<T> toTree(List<T> nodes, Object rootId, Comparator<T> comparator) {
        if (nodes == null || nodes.size() == 0) {
            return null;
        }
        Class<?> clz = nodes.get(0).getClass();
        Field[] fields = clz.getDeclaredFields();

        Field idField = null;
        Field parentIdField = null;
        Field childrenField = null;

        for (Field field : fields) {
            if (field.isAnnotationPresent(TreeId.class)) {
                idField = field;
                idField.setAccessible(true);
            }
            if (field.isAnnotationPresent(TreeParentId.class)) {
                parentIdField = field;
                parentIdField.setAccessible(true);
            }
            if (field.isAnnotationPresent(TreeChildren.class)) {
                childrenField = field;
                childrenField.setAccessible(true);
            }
        }

        if (idField == null) {
            throw new RuntimeException("树结构id字段不存在");
        }
        if (parentIdField == null) {
            throw new RuntimeException("树结构父id字段不存在");
        }
        if (childrenField == null) {
            throw new RuntimeException("树结构子节点字段不存在");
        }
        if (!childrenField.getType().isAssignableFrom(List.class)) {
            throw new RuntimeException("树结构子节点字段类型必须为列表");
        }
        ParameterizedType parameterizedType = (ParameterizedType) childrenField.getGenericType();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments[0].getClass() == nodes.get(0).getClass()) {
            throw new RuntimeException("树结构子节点字段泛型必须与节点本身的类型相同");
        }

        Field finalIdField = idField;
        List<Object> idList = nodes.stream().map(node -> {
            try {
                return finalIdField.get(node);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("树结构节点无法获取id值");
            }
        }).toList();

        Field finalParentIdField = parentIdField;
        Map<Object, List<T>> parentNodeMap = new HashMap<>();
        for (T node : nodes) {
            try {
                Object parentId = finalParentIdField.get(node);
                List<T> children = parentNodeMap.computeIfAbsent(parentId, k -> new ArrayList<>());
                children.add(node);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("树结构节点无法获取父id值");
            }
        }

        List<T> rootTrees = new ArrayList<>();
        if (rootId == null) {
            parentNodeMap.forEach((parentId, nodeList) -> {
                if (!idList.contains(parentId)) {
                    rootTrees.addAll(nodeList);
                }
            });
            if (rootTrees.size() == 0) {
                return rootTrees;
            }
        } else {
            if (parentNodeMap.get(rootId) == null) {
                return rootTrees;
            }
            rootTrees.addAll(parentNodeMap.get(rootId));
        }

        if (comparator != null) {
            rootTrees.sort(comparator);
        }

        for (T tree : rootTrees) {
            try {
                buildTree(tree, parentNodeMap, idField, childrenField, comparator);
            } catch (Exception e) {
                throw new RuntimeException("树结构节点无法获取id值");
            }
        }

        return rootTrees;
    }

    private static <T> void buildTree(T node, Map<Object, List<T>> parentNodeMap, Field idField, Field childrenField, Comparator<T> comparator) throws IllegalAccessException {
        Object id = idField.get(node);
        List<T> children = parentNodeMap.get(id);
        if (children == null) {
            return;
        }
        if (comparator != null) {
            children.sort(comparator);
        }
        childrenField.set(node, children);
        for (T child : children) {
            buildTree(child, parentNodeMap, idField, childrenField, comparator);
        }
    }

}
