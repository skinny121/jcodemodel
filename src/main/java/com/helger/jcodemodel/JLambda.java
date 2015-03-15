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
package com.helger.jcodemodel;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lambda Expression.
 *
 * Lambda expression can either have formal parameters in which case the parameters include there type,
 * name and can have annotations and modifiers. While informal parameters only include there name. There
 * type is inferred at compile time.
 */
public final class JLambda extends AbstractJExpressionImpl{
  private IJGenerable statement;
  private List<JVar> formalVars;
  private JVar varParam;
  private List<JLambdaParam> informalVars;
  private boolean formal;
  JLambda (IJGenerable statement, boolean formal){
    this.statement = statement;
    this.formal = formal;
    if(formal){
      formalVars = new ArrayList<JVar>();
      informalVars = null;
    }else{
      formalVars = null;
      informalVars = new ArrayList<JLambdaParam>();
    }
  }

  /**
   * Adds a informal parameter to the list of parameters.
   *
   * @param name
   *        Name of the lambda parameter
   * @return
   *         A lambda parameter variable
   * @throws IllegalStateException
   *         If this lambda expression uses formal parameters.
   */
  @Nonnull
  public JLambdaParam param (@Nonnull final String name){
    if(formal)
      throw new IllegalStateException("For formal parameters a type needs to be specified");
    JLambdaParam lambdaParam = new JLambdaParam(name);
    informalVars.add(lambdaParam);
    return lambdaParam;
  }

  /**
   * Adds a formal parameter to the list of parameters.
   *
   * @param mods
   *        Modifiers for this lambda parameter
   * @param type
   *        JType of the lambda parameter
   * @param name
   *        Name of the lambda parameter
   * @return
   *         A lambda parameter variable
   * @throws IllegalStateException
   *         If this lambda expression uses informal parameters.
   */
  @Nonnull
  public JVar param (final int mods, @Nonnull final AbstractJType type, @Nonnull final String name){
    if(!formal)
      throw new IllegalStateException ("Informal parameters don't need a type");
    JVar var = new JVar (JMods.forVar (mods), type, name, null);
    formalVars.add (var);
    return var;
  }

  @Nonnull
  public List<? extends IJAssignmentTarget> listParams (){
    if(formal)
      return Collections.unmodifiableList (formalVars);
    else
      return Collections.unmodifiableList (informalVars);
  }

  /**
   * Adds a formal vararg parameter to the list of parameters.
   *
   * @param mods
   *        Modifiers for this lambda parameter
   * @param type
   *        JType of the lambda parameter. Automatically converted to array type
   * @param name
   *        Name of the lambda parameter
   * @return
   *         A lambda parameter variable
   * @throws IllegalStateException
   *         If this lambda expression uses informal parameters or already has a vararg parameter.
   */
  @Nonnull
  public JVar varParam (final int mods, @Nonnull final AbstractJType type, @Nonnull final String name){
    if (!formal)
      throw new IllegalStateException ("Cannot add vararg to lambda expression with informal arguments");
    if (hasVarArgs ())
      throw new IllegalStateException ("Cannot have two varargs in a lambda,\n"
              + "Check if varParam method of JMethod is"
              + " invoked more than once");

    varParam = new JVar (JMods.forVar (mods), type.array (), name, null);
    return varParam;
  }

  /**
   * Returns if the lambda expression has vararg
   */
  public boolean hasVarArgs (){
    return varParam != null;
  }

  /**
   * Gets the vararg parameter.
   */
  public JVar varParam (){
    return varParam;
  }

  /**
   * Returns whether this lambda expression uses formal parameters.
   */
  public boolean isFormalArgs (){
    return formal;
  }

  /**
   * Sets the new body of the lambda expression. The body of the lambda
   * expression can be either a JBlock or any expression.
   * @param statement
   *        Statement the lambda expression is going to use
   * @return The old statement
   */
  public IJGenerable body (IJGenerable statement){
    IJGenerable old = this.statement;
    this.statement = statement;
    return old;
  }

  /**
   * Returns the body as a JBlock.
   *
   * @return A JBlock
   * @throws IllegalStateException
   *         If the lambdas body is an expression and not a JBlock
   */
  @Nonnull
  public JBlock block(){
    if(statement == null)
      body (new JBlock ());
    if(statement instanceof JBlock)
      return (JBlock)statement;
    else
      throw new IllegalStateException ("Body is not a JBlock");
  }

  /**
   * Returns the body of the lambda expression. This can either be a JBlock or any expression.
   * If no body has not yet been set then an empty JBlock is returned.
   */
  @Nonnull
  public IJGenerable body (){
    if(statement == null)
      body (new JBlock ());
    return statement;
  }

  /**
   * Converts the lambda expression to use informal parameters instead of formal ones.
   * If its already using informal parameters then this is a no-op.
   */
  public void toInformal(){
    if(!formal){
      formal = false;
      informalVars = new ArrayList<JLambdaParam> ();
      for(JVar var:formalVars){
        informalVars.add (new JLambdaParam (var.name ()));
      }
      formalVars = null;
    }
  }

  public void generate (@Nonnull JFormatter f) {
    int argLen = formal ? formalVars.size () : informalVars.size ();
    if(formal || argLen != 1)
      f.print ('(');
    boolean first = true;
    for(int i=0;i<argLen;i++){
      if (!first)
        f.print (", ");
      else
        first = false;
      if(formal)
        f.var (formalVars.get (i));
      else
        f.generable (informalVars.get (i));
    }
    if (hasVarArgs ()){
      if (!first)
        f.print (',');
      for (final JAnnotationUse annotation : varParam.annotations())
        f.generable(annotation);
      f.generable (varParam.mods ()).generable (varParam.type ().elementType ());
      f.print ("... ");
      f.id (varParam.name ());
    }
    if(formal || argLen != 1)
      f.print (')');
    f.print(" -> ");
    if(statement instanceof JBlock && block().isEmpty()){
      f.print("{}");
    }else{
      f.indent ().generable (body()).outdent ();
    }
  }
}
