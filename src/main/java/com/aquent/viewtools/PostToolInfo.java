package com.aquent.viewtools;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.servlet.ServletToolInfo;

public class PostToolInfo extends ServletToolInfo {

    @Override
    public String getKey () {
        return "post";
    }

    @Override
    public String getScope () {
        return ViewContext.APPLICATION;
    }

    @Override
    public String getClassname () {
        return PostTool.class.getName();
    }

    @Override
    public Object getInstance ( Object initData ) {

        PostTool viewTool = new PostTool();
        viewTool.init( initData );

        setScope( ViewContext.APPLICATION );

        return viewTool;
    }

}
