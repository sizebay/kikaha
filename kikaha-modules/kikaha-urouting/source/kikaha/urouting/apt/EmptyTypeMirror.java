package kikaha.urouting.apt;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;

class EmptyTypeMirror implements TypeMirror {

	public TypeKind getKind(){
		return null;
	}

	public <R, P> R accept(TypeVisitor<R, P> v, P p) {
		return null;
	}
	
	public List<? extends AnnotationMirror> getAnnotationMirrors() {
		return null;
	}
	
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		return null;
	}
	
	public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
		return null;
	}
	
	@Override
	public String toString() {
		return "";
	}
}
