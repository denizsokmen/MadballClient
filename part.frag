#version 110


uniform float dist;
uniform sampler2D tex;
varying vec3 normal;
void main()
{
	float red= clamp((dist / 100.0),0.0,0.8);

	vec4 texcol=texture2D(tex, gl_TexCoord[0].st);
	if (texcol.x > 0.8 && texcol.y > 0.8 && texcol.z > 0.8  )
		gl_FragColor=vec4(0.0,0.0,0.0,0.0) ;
		else
		gl_FragColor=vec4(1.0,red,0.0,1.0- (red*red)) ;

}
