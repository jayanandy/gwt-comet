/*
 * Copyright 2009 Richard Zschech.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.zschech.gwt.comet.rebind;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;

import net.zschech.gwt.comet.client.SerialMode;
import net.zschech.gwt.comet.client.SerialTypes;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.javac.TypeOracleMediator;
import com.google.gwt.dev.util.collect.Lists;
import com.google.gwt.rpc.linker.RpcDataArtifact;
import com.google.gwt.user.client.rpc.impl.Serializer;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.rpc.SerializableTypeOracle;
import com.google.gwt.user.rebind.rpc.SerializableTypeOracleBuilder;
import com.google.gwt.user.rebind.rpc.SerializationUtils;
import com.google.gwt.user.rebind.rpc.TypeSerializerCreator;

public class CometSerializerGenerator extends Generator {
	
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
		
		TypeOracle typeOracle = context.getTypeOracle();
		
		// Create the CometSerializer impl
		String packageName = "comet";
		String className = typeName.replace('.', '_') + "Impl";
		PrintWriter printWriter = context.tryCreate(logger, packageName, className);
		
		if (printWriter != null) {
			
			try {
				JClassType type = typeOracle.getType(typeName);
				SerialTypes annotation = type.getAnnotation(SerialTypes.class);
				if (annotation == null) {
					logger.log(TreeLogger.ERROR, "No SerialTypes annotation on CometSerializer type: " + typeName);
					throw new UnableToCompleteException();
				}
				
				SerializableTypeOracleBuilder typesSentToBrowserBuilder = new SerializableTypeOracleBuilder(logger, context.getPropertyOracle(), typeOracle);
				SerializableTypeOracleBuilder typesSentFromBrowserBuilder = new SerializableTypeOracleBuilder(logger, context.getPropertyOracle(), typeOracle);
				
				for (Class<? extends Serializable> serializable : annotation.value()) {
					int rank = 0;
					if (serializable.isArray()) {
						while(serializable.isArray()) {
							serializable = (Class<? extends Serializable>) serializable.getComponentType();
							rank++;
						}
					}
						
					JType resolvedType = typeOracle.getType(serializable.getCanonicalName());
					while (rank > 0) {
						resolvedType = typeOracle.getArrayType(resolvedType);
						rank--;
					}
					
					typesSentToBrowserBuilder.addRootType(logger, resolvedType);
				}
				
				// Create a resource file to receive all of the serialization information
				// computed by STOB and mark it as private so it does not end up in the
				// output.
				OutputStream pathInfo = context.tryCreateResource(logger, typeName + ".rpc.log");
				typesSentToBrowserBuilder.setLogOutputStream(pathInfo);
				typesSentFromBrowserBuilder.setLogOutputStream(pathInfo);
				
				SerializableTypeOracle typesSentToBrowser = typesSentToBrowserBuilder.build(logger);
				SerializableTypeOracle typesSentFromBrowser = typesSentFromBrowserBuilder.build(logger);
				
				if (pathInfo != null) {
					context.commitResource(logger, pathInfo).setPrivate(true);
				}
				
				// Create the serializer
				TypeSerializerCreator tsc = new TypeSerializerCreator(logger, typesSentFromBrowser, typesSentToBrowser, context, "comet." + typeName.replace('.', '_') + "Serializer");
				String realize = tsc.realize(logger);
				
				// Create the CometSerializer impl
				ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, className);
				
				composerFactory.addImport(Serializer.class.getName());
				composerFactory.addImport(SerialMode.class.getName());
				
				composerFactory.setSuperclass(typeName);
				// TODO is the SERIALIZER required for DE RPC?
				SourceWriter sourceWriter = composerFactory.createSourceWriter(context, printWriter);
				sourceWriter.print("private Serializer SERIALIZER = new " + realize + "();");
				sourceWriter.print("protected Serializer getSerializer() {return SERIALIZER;}");
				sourceWriter.print("public SerialMode getMode() {return SerialMode." + annotation.mode().name() + ";}");
				sourceWriter.commit(logger);
				
				if (annotation.mode() == SerialMode.DE_RPC) {
					RpcDataArtifact data = new RpcDataArtifact(type.getQualifiedSourceName());
					for (JType t : typesSentToBrowser.getSerializableTypes()) {
						if (!(t instanceof JClassType)) {
							continue;
						}
						JField[] serializableFields = SerializationUtils.getSerializableFields(context.getTypeOracle(), (JClassType) t);
						
						List<String> names = Lists.create();
						for (int i = 0, j = serializableFields.length; i < j; i++) {
							names = Lists.add(names, serializableFields[i].getName());
						}
						
						data.setFields(TypeOracleMediator.computeBinaryClassName(t), names);
					}
					
					context.commitArtifact(logger, data);
				}
			}
			catch (NotFoundException e) {
				logger.log(TreeLogger.ERROR, "", e);
				throw new UnableToCompleteException();
			}
		}
		
		return packageName + '.' + className;
	}
}