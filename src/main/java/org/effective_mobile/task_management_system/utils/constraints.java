package org.effective_mobile.task_management_system.utils;

public class constraints {
    public static class length {
        public static class user {
            public static class username {
                public static final int MIN = 3;
                public static final int MAX = 30;
            }

            public static class email {
                public static final int MIN = 10;
                public static final int MAX = 40;
            }

            public static class password {
                public static final int MIN = 8;
            }
        }

        public static class task {
            public static class content {
                public static final int MIN = 2;
                public static final int MAX = 500;
            }
        }
    }

}
