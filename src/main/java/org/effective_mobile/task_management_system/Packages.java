package org.effective_mobile.task_management_system;

import org.apache.commons.lang3.StringUtils;

public interface Packages {
    String ROOT           = "org.effective_mobile.task_management_system";

    String RESOURCE       = ROOT + "." + "resource";
    String SERVICE        = ROOT + "." + "service";
    String SECURITY       = ROOT + "." + "security";
    String MAINTAIN       = ROOT + "." + "maintain";
    String CONFING        = ROOT + "." + "confing";
    String COMPONENT      = ROOT + "." + "component";
    String REPOSITORY     = ROOT + "." + "database.repository";
    String EXCEPTION      = ROOT + "." + "exception";
    String CACHE          = ROOT + "." + MAINTAIN + "." + "cache";
    String LOGGING        = ROOT + "." + MAINTAIN + "." + "logging";
}
