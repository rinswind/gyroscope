package com.prosyst.mprm.backend.proxy.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.*;

import com.prosyst.mprm.backend.proxy.gen.Proxy;
import com.prosyst.mprm.backend.proxy.gen.ProxyException;
import com.prosyst.mprm.backend.proxy.ref.Ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ProxyClassBuilder implements Opcodes {
  private static final String PROXY_IFACE;
  private static final String PROXY_CONTROL;
  private static final String PROXY_CONTROL_DESC;
  
  private static final String REF_IFACE;
  private static final String REF_LOCK;
  private static final String REF_LOCK_DESC;
  private static final String REF_DELEGATE;
  private static final String REF_DELEGATE_DESC;
  
  static {
    try {
      /* Init the Proxy constants */
      PROXY_IFACE = toInternalName(Proxy.class);
      
      PROXY_CONTROL = Proxy.class.getMethod("proxyControl", new Class[0]).getName(); 
      PROXY_CONTROL_DESC = "()L" + toInternalName(Ref.class) + ";";
      
      /* Init the DynamicRef constants */
      REF_IFACE = toInternalName(Ref.class.getName());
      
      REF_LOCK = Ref.class.getMethod("lock", new Class[0]).getName();
      REF_LOCK_DESC = "()L" + toInternalName(Lock.class) + ";";
      
      REF_DELEGATE = Ref.class.getMethod("delegate", new Class[0]).getName();
      REF_DELEGATE_DESC = "()L" + toInternalName(Object.class) + ";";
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  private static final String FIELD_DESC = "L" + REF_IFACE + ";";
  
  /**
   * 
   */
  private class MixinGenerator implements ClassVisitor {
    private final String ifName;
    private final String fieldName;
    private final Set<String> visitedMethods;
    
    public MixinGenerator(String ifName) {
      this.ifName = toInternalName(ifName);
      this.fieldName = toIdentifier(ifName);
      this.visitedMethods = new HashSet<String>();
    }
    
    /**
     * @param mv
     * @param fieldNo
     */
    public void generateConstructorCode(MethodVisitor mv, int fieldNo) {
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, fieldNo);
      mv.visitFieldInsn(PUTFIELD, implName, fieldName, FIELD_DESC);
    }
    
    /**
     * 
     */
    public void generateProxyControl() {
      MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, PROXY_CONTROL, PROXY_CONTROL_DESC, null, null);
      mv.visitCode();
      
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, implName, fieldName, FIELD_DESC);
      mv.visitInsn(ARETURN);
      
      mv.visitMaxs(0, 0);
      mv.visitEnd();
    }

    /**
     * 
     */
    public void generateHashCode() {
      generateMethod(ACC_PUBLIC, "hashCode", "()I", null, null);
    }
    
    /**
     * 
     */
    public void generateToString() {
      generateMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
    }
    
    /**
     * 
     */
    public void generateEquals() {
      visitedMethods.add("equals" + "(Ljava/lang/Object;)Z");
      
      MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "equals", "(Ljava/lang/Object;)Z", null, null);
      mv.visitCode();
      
      Label l0 = new Label();
      Label l1 = new Label();
      mv.visitTryCatchBlock(l0, l1, l1, null);
      Label l2 = new Label();
      Label l3 = new Label();
      mv.visitTryCatchBlock(l2, l3, l3, null);
      mv.visitInsn(ACONST_NULL);
      mv.visitVarInsn(ASTORE, 2);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, implName, fieldName, FIELD_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, REF_IFACE, REF_LOCK, REF_LOCK_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, "java/util/concurrent/locks/Lock", "lock", "()V");
      mv.visitLabel(l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, implName, fieldName, FIELD_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, REF_IFACE, REF_DELEGATE, REF_DELEGATE_DESC);
      mv.visitVarInsn(ASTORE, 2);
      Label l4 = new Label();
      mv.visitJumpInsn(GOTO, l4);
      mv.visitLabel(l1);
      mv.visitVarInsn(ASTORE, 3);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, implName, fieldName, FIELD_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, REF_IFACE, REF_LOCK, REF_LOCK_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, "java/util/concurrent/locks/Lock", "unlock", "()V");
      mv.visitVarInsn(ALOAD, 3);
      mv.visitInsn(ATHROW);
      mv.visitLabel(l4);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, implName, fieldName, FIELD_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, REF_IFACE, REF_LOCK, REF_LOCK_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, "java/util/concurrent/locks/Lock", "unlock", "()V");
      mv.visitVarInsn(ALOAD, 1);
      mv.visitTypeInsn(INSTANCEOF, PROXY_IFACE);
      Label l5 = new Label();
      mv.visitJumpInsn(IFEQ, l5);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitTypeInsn(CHECKCAST, PROXY_IFACE);
      mv.visitMethodInsn(INVOKEINTERFACE, PROXY_IFACE, PROXY_CONTROL, PROXY_CONTROL_DESC);
      mv.visitVarInsn(ASTORE, 3);
      mv.visitVarInsn(ALOAD, 3);
      mv.visitMethodInsn(INVOKEINTERFACE, REF_IFACE, REF_LOCK, REF_LOCK_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, "java/util/concurrent/locks/Lock", "lock", "()V");
      mv.visitLabel(l2);
      mv.visitVarInsn(ALOAD, 3);
      mv.visitMethodInsn(INVOKEINTERFACE, REF_IFACE, REF_DELEGATE, REF_DELEGATE_DESC);
      mv.visitVarInsn(ASTORE, 1);
      Label l6 = new Label();
      mv.visitJumpInsn(GOTO, l6);
      mv.visitLabel(l3);
      mv.visitVarInsn(ASTORE, 4);
      mv.visitVarInsn(ALOAD, 3);
      mv.visitMethodInsn(INVOKEINTERFACE, REF_IFACE, REF_LOCK, REF_LOCK_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, "java/util/concurrent/locks/Lock", "unlock", "()V");
      mv.visitVarInsn(ALOAD, 4);
      mv.visitInsn(ATHROW);
      mv.visitLabel(l6);
      mv.visitVarInsn(ALOAD, 3);
      mv.visitMethodInsn(INVOKEINTERFACE, REF_IFACE, REF_LOCK, REF_LOCK_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, "java/util/concurrent/locks/Lock", "unlock", "()V");
      mv.visitLabel(l5);
      mv.visitVarInsn(ALOAD, 2);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z");
      mv.visitInsn(IRETURN);
      
      mv.visitMaxs(0, 0);
      mv.visitEnd();
    }
    
    /**
     *  
     */
    public void generateMixin() {
      /* Generate the field */
      cv.visitField(ACC_PRIVATE + ACC_FINAL, fieldName, FIELD_DESC, null, null).visitEnd();
      
      /* Visit the interface */
      getReader(ifName).accept(this, null, ClassReader.SKIP_DEBUG);
    }

    public void visit(int ver, int acc, String name, String sig, String superN, String[] ifaces) {
      /*
       * Visit the super interfaces before the main interface. This yields a depth
       * first left to right traversal of the interface acyclic directed graph
       */
      for (int i = 0; i < ifaces.length; i++) {
        getReader(ifaces[i]).accept(this, null, ClassReader.SKIP_DEBUG);
      }
    }

    public void visitSource(String source, String debug) {
      return;
    }

    public void visitOuterClass(String owner, String name, String desc) {
      return;
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
      return null;
    }

    public void visitAttribute(Attribute attr) {
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
    }

    public FieldVisitor visitField(int access, String name, String desc, String sig, Object val) {
      return null;
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] excs) {
      generateMethod(access, name, desc, sig, excs);
      return null;
    }
    
    public void visitEnd() {
    }
    
    private void generateMethod(int access, String name, String desc, String sig, String[] excs) {
      String methodSig = name + desc;
      if (visitedMethods.contains(methodSig)) {
        return;
      }
      
      MethodVisitor mv = cv.visitMethod(access & ~ACC_ABSTRACT, name, desc, sig, excs);
      mv.visitCode();
      
      Label l0 = new Label();
      Label l1 = new Label();
      mv.visitTryCatchBlock(l0, l1, l1, null);
      
      /* Lock */
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, implName, fieldName, FIELD_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, REF_IFACE, REF_LOCK, REF_LOCK_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, "java/util/concurrent/locks/Lock", "lock", "()V");
      mv.visitLabel(l0);
      
      /* Dereference */
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, implName, fieldName, FIELD_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, REF_IFACE, REF_DELEGATE, REF_DELEGATE_DESC);
      mv.visitTypeInsn(CHECKCAST, ifName);
      
      /* Invoke */
      Type[] args = Type.getArgumentTypes(desc);
      for (int i = 0; i < args.length; i++) {
        mv.visitVarInsn(args[i].getOpcode(ILOAD), i + 1);
      }
      mv.visitMethodInsn(INVOKEINTERFACE, ifName, name, desc);
      
      Label l2 = new Label();
      mv.visitJumpInsn(GOTO, l2);
      
      /* Unlock and re-throw */
      mv.visitLabel(l1);
      mv.visitVarInsn(ASTORE, args.length + 1);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, implName, fieldName, FIELD_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, REF_IFACE, REF_LOCK, REF_LOCK_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, "java/util/concurrent/locks/Lock", "unlock", "()V");
      mv.visitVarInsn(ALOAD, args.length + 1);
      mv.visitInsn(ATHROW);
      
      /* Unlock and return */
      mv.visitLabel(l2);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, implName, fieldName, FIELD_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, REF_IFACE, REF_LOCK, REF_LOCK_DESC);
      mv.visitMethodInsn(INVOKEINTERFACE, "java/util/concurrent/locks/Lock", "unlock", "()V");
      mv.visitInsn(Type.getReturnType(desc).getOpcode(IRETURN));
      
      mv.visitMaxs(0, 0);
      mv.visitEnd();
      
      visitedMethods.add(methodSig);
    }
  }
  
  private final String implName;
  private final ClassLoader loader;
  
  private final List<MixinGenerator> mixins; 
  private final List<String> classSig;
  
  private String constrSig = "";
  
  private final ClassWriter cv;
  
  /**
   * @param implName
   * @param loader
   */
  public ProxyClassBuilder(String implName, ClassLoader loader) {
    this.implName = toInternalName(implName);
    this.loader = loader;
    
    this.mixins = new ArrayList<MixinGenerator>();
    this.classSig = new ArrayList<String>();
    
    this.cv = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
  }
  
  /**
   * @param ifName
   * @param dynamic
   */
  public void add(String ifName) {
    ifName = toInternalName(ifName);
    
    boolean first = mixins.size() == 0;
    
    MixinGenerator e = new MixinGenerator(ifName);
    mixins.add(e);
    classSig.add(ifName);
    
    /* Add the mixin to the constructor signature */
    constrSig += first ? "(" : "";
    constrSig += "L" + REF_IFACE + ";";
  }
  
  /**
   * @return
   */
  public byte[] generate() {
    classSig.add(PROXY_IFACE);
    constrSig += ")V";
   
    /* Write the class header */
    cv.visit(V1_5, ACC_PUBLIC + ACC_SUPER, implName, null, "java/lang/Object", 
        (String[]) classSig.toArray(new String[classSig.size()]));
    
    /* Start the constructor */
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "<init>", constrSig, null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
    
    /*
     * Add a field to hold the delegate followed by implementations of all
     * methods that use that field for delegation
     */
    for (int no = 0; no < mixins.size(); no++) {
      MixinGenerator e = (MixinGenerator) mixins.get(no);
      
      /*
       * First mixin our overrides of object methods so that this is remembered
       * and later mixing does not add them again.
       */
      if (no == 0) {
        e.generateProxyControl();
        e.generateToString();
        e.generateHashCode();
        e.generateEquals();
      }
      
      e.generateConstructorCode(mv, no + 1);
      e.generateMixin();
    }
    
    /* Finish the constructor */
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
    
    cv.visitEnd();
    
    return cv.toByteArray();
  }
  
  /**
   * @param name
   * @return
   */
  private ClassReader getReader(String name) {
    try {
      String path = name + ".class";
      
      InputStream str = ClassLoader.getSystemResourceAsStream(path);
      
      if (str == null) {
        ClassLoader otherLoader = loader.loadClass(toClassName(name)).getClassLoader();
        str = otherLoader.getResourceAsStream(path);
      }

      return new ClassReader(str);
    } catch (IOException e) {
      throw new ProxyException(e);
    } catch (ClassNotFoundException e) {
      throw new ProxyException(e);
    }    
  }
  
  /**
   * @param name
   * @return
   */
  private static String toInternalName(Class<?> cl) {
    return toInternalName(cl.getName());
  }
  
  /**
   * @param name
   * @return
   */
  private static String toInternalName(String name) {
    return name.replace('.', '/');
  }
  
  /**
   * @param name
   * @return
   */
  private static String toClassName(String name) {
    return name.replace('/', '.');
  }
  
  /**
   * @param name
   * @return
   */
  private static String toIdentifier(String name) {
    return name.replace('.', '_').replace('/', '_');
  }
}
