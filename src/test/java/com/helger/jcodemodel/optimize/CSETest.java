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
package com.helger.jcodemodel.optimize;

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr.lit;
import static com.helger.jcodemodel.JExpr.ref;

import java.util.HashSet;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.jcodemodel.AbstractJExpressionImpl;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JConditional;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JForLoop;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.tests.util.CodeModelTestsUtils;

public final class CSETest
{
  /**
   * Run tests one by one and verify they do something sensible.
   */
  private static void _println (@Nonnull final JCodeModel cm,
                                @Nonnull final JBlock b,
                                @Nonnull final IJExpression expression)
  {
    b.invoke (cm.ref (System.class).staticRef ("out"), "println").arg (expression).hintType (cm.VOID);
  }

  @Test
  public void testFieldRefObjectInvalidation () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestFieldRefObjectInvalidation");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    final JBlock b = m.body ();

    final JVar a = b.decl ("a", JExpr.newArray (cm.INT, 42));
    final AbstractJExpressionImpl aLength = ref (a, "length").hintType (cm.INT);

    _println (cm, b, aLength);
    _println (cm, b, aLength);

    b.assign (a, JExpr.newArray (cm.INT, 21));
    _println (cm, b, aLength);

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }

  @Test
  public void testArrayIndexingInvalidation () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestArrayIndexingInvalidation");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    final JBlock b = m.body ();

    final JVar a = b.decl ("a", JExpr.newArray (cm.INT, 42));
    final JVar i = b.decl ("i", lit (42));

    _println (cm, b, a.component (i));
    _println (cm, b, a.component (i));

    b.assign (i, lit (21));
    _println (cm, b, a.component (i));
    _println (cm, b, a.component (i));

    b.assign (a, JExpr.newArray (cm.INT, 21));
    _println (cm, b, a.component (i));

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }

  @Test
  public void testMethodCallInvalidation () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestMethodCallArgumentInvalidation");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    final JBlock b = m.body ();

    final JVar map = b.decl ("map", _new (cm._ref (HashSet.class)));
    final JVar i = b.decl ("i", lit (42));

    _println (cm, b, map.invoke ("contains").arg (i).hintType (cm.BOOLEAN));
    _println (cm, b, map.invoke ("contains").arg (i).hintType (cm.BOOLEAN));

    b.assign (i, lit (21));
    _println (cm, b, map.invoke ("contains").arg (i).hintType (cm.BOOLEAN));
    _println (cm, b, map.invoke ("contains").arg (i).hintType (cm.BOOLEAN));

    b.assign (map, _new (cm._ref (HashSet.class)));
    _println (cm, b, map.invoke ("contains").arg (i).hintType (cm.BOOLEAN));

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }

  @Test
  public void testNestedSubExpressions () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestNestedSubExpressions");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    final JBlock b = m.body ();

    b.invoke (cm.ref (System.class).staticRef ("out"), "println")
     .arg (ref ("arrayField").hintType (cm.LONG.array ()).component (ref ("indexField").hintType (cm.INT)))
     .hintType (cm.VOID);
    b.invoke (cm.ref (System.class).staticRef ("out"), "println")
     .arg (ref ("arrayField").hintType (cm.LONG.array ()).component (ref ("indexField").hintType (cm.INT)))
     .hintType (cm.VOID);

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }

  @Test
  public void testSubBlockDefinitionPull () throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestSubBlockDefinitionPull");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    final JBlock b = m.body ();

    final JVar a = b.decl ("a", JExpr.newArray (cm.INT, 42));
    final AbstractJExpressionImpl aLength = ref (a, "length").hintType (cm.INT);

    final JBlock subBlock = b.block ();
    // without dummy definition jCodeModel won't create a sub block
    subBlock.decl ("i", lit (1));
    _println (cm, subBlock, aLength);
    _println (cm, b, aLength);

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }

  @Test
  public void testInsideSubBlockInvalidation () throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestInsideSubBlockInvalidation");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    final JBlock b = m.body ();

    final JVar a = b.decl ("a", JExpr.newArray (cm.INT, 42));
    final AbstractJExpressionImpl aLength = ref (a, "length").hintType (cm.INT);

    _println (cm, b, aLength);

    final JBlock subBlock = b.block ();
    // without dummy definition jCodeModel won't create a sub block
    subBlock.decl ("i", lit (1));
    subBlock.assign (a, JExpr.newArray (cm.INT, 21));
    _println (cm, subBlock, aLength);
    _println (cm, subBlock, aLength);

    _println (cm, b, aLength);

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }

  @Test
  public void testIfBranchesPull () throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestIfBranchesPull");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    final JBlock b = m.body ();

    final JVar a = b.decl ("a", JExpr.newArray (cm.INT, 42));
    final AbstractJExpressionImpl aLength = ref (a, "length").hintType (cm.INT);

    JConditional cond = b._if (cls.staticInvoke ("test"));
    _println (cm, cond._then (), aLength);
    _println (cm, cond._else (), aLength);

    b.assign (a, JExpr.newArray (cm.INT, 21));

    cond = b._if (cls.staticInvoke ("test"));
    _println (cm, cond._then (), aLength);
    cond._else ().decl ("i", lit (1));

    cond = b._if (cls.staticInvoke ("test"));
    JConditional cond2 = cond._then ()._if (cls.staticInvoke ("test"));
    _println (cm, cond2._then (), aLength);
    _println (cm, cond2._else (), aLength);

    JConditional cond3 = cond._else ()._if (cls.staticInvoke ("test"));
    _println (cm, cond3._then (), aLength);
    _println (cm, cond3._else (), aLength);

    b.assign (a, JExpr.newArray (cm.INT, 42));

    cond = b._if (cls.staticInvoke ("test"));
    cond2 = cond._then ()._if (cls.staticInvoke ("test"));
    _println (cm, cond2._then (), aLength);
    _println (cm, cond2._else (), aLength);

    cond3 = cond._else ()._if (cls.staticInvoke ("test"));
    _println (cm, cond3._then (), aLength);
    cond3._else ().decl ("i", lit (1));

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }

  @Test
  public void testForLoop () throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestForLoop");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    final JBlock b = m.body ();

    final JVar a = b.decl ("a", JExpr.newArray (cm.INT, 42));
    final JVar j = b.decl ("j", lit (42));

    final JForLoop _for = b._for ();
    final JVar i = _for.init (cm.INT, "i", lit (0));
    _for.test (i.lt (lit (10)));
    _for.update (i.incr ());
    final JBlock forBody = _for.body ();
    _println (cm, forBody, a.component (i));
    _println (cm, forBody, a.component (j));

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }
}
