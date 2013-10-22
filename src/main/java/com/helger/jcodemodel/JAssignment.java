/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package com.helger.jcodemodel;

import javax.annotation.Nonnull;

/**
 * Assignment statements, which are also expressions.
 */
public class JAssignment extends AbstractJExpressionImpl implements IJExpressionStatement
{
  private final IJAssignmentTarget lhs;
  private final IJExpression rhs;
  private final String op;

  /**
   * Constructor for "=" operator
   * 
   * @param lhs
   *        left
   * @param rhs
   *        right
   */
  protected JAssignment (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    this (lhs, rhs, "");
  }

  /**
   * Constructor for <code>op + "="</code> operator
   * 
   * @param lhs
   *        left
   * @param rhs
   *        right
   * @param op
   *        additional operator
   */
  protected JAssignment (@Nonnull final IJAssignmentTarget lhs,
                         @Nonnull final IJExpression rhs,
                         @Nonnull final String op)
  {
    this.lhs = lhs;
    this.rhs = rhs;
    this.op = op;
  }

  @Nonnull
  public IJAssignmentTarget lhs ()
  {
    return lhs;
  }

  @Nonnull
  public IJExpression rhs ()
  {
    return rhs;
  }

  /**
   * @return The additional operator (without the "=")
   */
  @Nonnull
  public String op ()
  {
    return op;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.generable (lhs).print (op + '=').generable (rhs);
  }

  public void state (@Nonnull final JFormatter f)
  {
    f.generable (this).print (';').newline ();
  }
}
