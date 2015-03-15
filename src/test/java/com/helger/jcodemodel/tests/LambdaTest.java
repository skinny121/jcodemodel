/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2015 Philip Helger
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.helger.jcodemodel.tests;

import com.helger.jcodemodel.*;
import com.helger.jcodemodel.writer.SingleStreamCodeWriter;
import org.junit.Test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LambdaTest {

  @Test
  public void main() throws Exception{
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass c = cm._class ("Test");

    final JMethod m = c.method (JMod.PUBLIC, cm.VOID, "foo");

    m.body ().decl (cm.ref (Runnable.class), "runnable", JExpr.lambda ());

    final JFieldRef sysOut = cm.ref (System.class).staticRef ("out");
    final JInvocation printInvoke = JExpr.invoke (sysOut, "println");
    printInvoke.arg ("Hello");

    m.body ().decl (cm._ref(Runnable.class), "runnable1", JExpr.lambda (printInvoke));

    final JLambda lambda = JExpr.lambda ();
    final JLambdaParam e = lambda.param ("e");
    final JInvocation printActionInvoke = JExpr.invoke (sysOut, "println");
    printActionInvoke.arg (e);
    lambda.block ()._if (JOp.eq(e, JExpr._null ()))._then ().add (printActionInvoke);
    m.body ().decl (cm.ref (ActionListener.class), "al", lambda);

    final JLambda lambda1 = JExpr.lambda (true);
    final JVar e1 = lambda1.param (JMod.FINAL, cm.ref (ActionEvent.class), "e1");
    m.body ().decl (cm.ref (ActionListener.class), "al1", lambda1);

    cm.build (new SingleStreamCodeWriter (System.out));
  }
}
