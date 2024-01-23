package org.effective_mobile.task_management_system.utils;


public final class Api {
    public static final String SIGN_IN = "/signin";
    public static final String SIGN_UP = "/signup";
    public static final String SIGN_OUT = "/signout";
    public static final String TASK = "/task";

    public static final String EXECUTOR = "/executor";
    public static final String UNASSIGN = "/unassign";
    public static final String STATUS = "/status";
    public static final String PRIORITIES = "/priorities";
    public static final String COMMENT = "/comment";

    public static final class QueryParam {
        public static final String NEW_STATUS = "value";
        public static final class Page {
            public static final String NAME = "page";
            public static final String DEFAULT_INDEX = "0";
            public static final String SIZE = "size";
            public static final String DEFAULT_SIZE = "10";
        }
    }

    public static final class PathParam {
        public static final String COMMENT_TASK_ID = "task_id";
        public static final String ID = "id";
    }

}
