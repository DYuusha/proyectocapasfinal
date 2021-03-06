package com.uca.capas.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import com.uca.capas.domain.Usuario;
import com.uca.capas.domain.UsuarioBeneficiario;
import com.uca.capas.domain.Admin;
import com.uca.capas.domain.Operacion;
import com.uca.capas.repositories.AdminRepository;
import com.uca.capas.repositories.OperacionRepository;
import com.uca.capas.repositories.UsuarioBenefRepository;
import com.uca.capas.repositories.UsuarioRepository;

@Controller
@SessionAttributes("tought")

public class MainController {
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private UsuarioBenefRepository usuarioBenefRepository;
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private OperacionRepository operacionRepository;
	Calendar cal = Calendar.getInstance();
	Usuario publico = new Usuario(1,"cesar","1234","cesarLima","1234d4",cal,120.00,true);
	
	
	@RequestMapping("/index")
	public ModelAndView initMain() {
		ModelAndView mv = new ModelAndView();
		List<Usuario> beneficiario = usuarioRepository.findBeneficiarioByUsuario(publico.getIdUsuario());
		mv.addObject("usuario", publico);
		mv.addObject("beneficiario", beneficiario);
		mv.setViewName("transferencia");
		return mv;
	}
	@RequestMapping("/listaBenef")
	public ModelAndView listaBenef() {
		ModelAndView mv = new ModelAndView();
		/*List<Usuario> usuarios = usuarioRepository.findBeneficiarioNOTUsuario(publico.getIdUsuario());
		if(usuarios.isEmpty()) {*/
			List<Usuario> todos = usuarioRepository.findBeneficiarioNotEqualUsuario(publico.getIdUsuario());
			mv.addObject("usuarios", todos);
	/*	}
		else {
			mv.addObject("usuarios", usuarios);
		}		*/
		mv.setViewName("agregarbeneficiario");
		return mv;
	}

	@RequestMapping(value="/agregarBenef", method = RequestMethod.POST)
	public ModelAndView agregarBenef(@RequestParam String numerocuenta) {
		ModelAndView mv = new ModelAndView();
		Usuario v = usuarioRepository.findBynumCuenta(numerocuenta);
		UsuarioBeneficiario a= new UsuarioBeneficiario(publico.getIdUsuario(),v.getIdUsuario());

			usuarioBenefRepository.save(a);
			mv.addObject("resultado", 0);
			mv.addObject("usuario", publico);
			List<Usuario> beneficiario = usuarioRepository.findBeneficiarioByUsuario(publico.getIdUsuario());
			mv.addObject("beneficiario", beneficiario);
			mv.setViewName("transferencia");
		return mv;
	}
	@RequestMapping(value="/agregarTransferencia", method = RequestMethod.POST)
	public ModelAndView agregarTransferencia(@RequestParam String monto, @RequestParam String concepto, @RequestParam Integer benef) {
		ModelAndView mv = new ModelAndView();
		Calendar cal = Calendar.getInstance();
		Double mont= Double.parseDouble(monto);
		cal.add(Calendar.DATE, 1);
		SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
		String formatted = format1.format(cal.getTime());
		try {
			cal.setTime(format1.parse(formatted));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Operacion a = new Operacion(0,cal,mont,concepto,publico.getIdUsuario(),benef,0);
		operacionRepository.save(a);
		mv.setViewName("transferencia");
		return mv;
	}
	
	
	@RequestMapping("/validate")
	public ModelAndView validate(@RequestParam String user, @RequestParam String pass){
		ModelAndView mav = new ModelAndView();
		
		String nvista ="";
		Admin a = adminRepository.findBySusernameAndSpassword(user, pass);
		//Usuario u = usuarioRepository.findByUsernameAndPassword(user, pass);
		
		if(a!=null) {
			nvista="admin";
		}
		/*else if(u!=null) {
			nvista="cuenta";
			
		}*/
		else {
			nvista="index";
		}
		
		mav.setViewName(nvista);

		return mav;
	}

}
