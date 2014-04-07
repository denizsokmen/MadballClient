#version 110

uniform sampler2D tex;
varying vec3 normal;
void main()
{
	vec4 texcol=texture2D(tex, gl_TexCoord[0].st);
	vec4 col=texture2D(tex, gl_TexCoord[0].st) ;
	col += vec4(1.0,0.0,0.0,0.0);
	//if (texcol.x > 0.99 && texcol.y < 0.5 && texcol.z < 0.5  )
	//	col=vec4(0.0,0.0,0.0,0.0);
	
	
	gl_FragColor=col ;

}
