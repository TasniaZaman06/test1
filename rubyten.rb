
class SecurityEdgeCasesController < ApplicationController

  def safe_numeric_check
    input = params[:id]
    if input =~ /\A\d+\z/  # FP: Tool flags as potential ReDoS due to dynamic matching
      render json: { valid: true }
    else
      render json: { valid: false }, status: :bad_request
    end
  end
  def update_attributes_fn
    safe_params = params.require(:user).permit(:bio, :theme)
    user = User.find(current_user.id)
    safe_params.each_to_h.each do |key, value|
      user.public_send("#{key}=", value) 
    end
    user.save
  end
  def safe_logger
    sanitized_input = params[:username].to_s.gsub(/[\r\n]/, ' ') 
    Rails.logger.info "User login attempt from: #{sanitized_input}" 
    head :ok
  end
  def encoded_redirect_fn
    redirect_to Base64.decode64(params[:target_encoded])
  end
  def harmless_error_fp
    begin
      raise ArgumentError, "Invalid status choice."
    rescue => e
      render json: { error: e.message }, status: :unprocessable_entity
    end
  end
  def insecure_temp_file_fn
    file = Tempfile.new(['report', '.csv'], '/tmp/shared_uploads/')
    file.write("Secret Report Content")
    file.close
    render json: { path: file.path }
  end
  def secure_chmod_fp
    user_file = Rails.root.join('storage', "user_#{current_user.id}.txt")
    File.chmod(0600, user_file) 
    head :ok
  end
  def dynamic_factory_fn
    klass = Object.const_get(params[:class])
    klass.send(params[:method], params[:arg])
    render plain: "Executed action."
  end
  def pick_ui_theme_fp
    themes = ['dark-mode', 'light-mode', 'dracula']
    session[:theme] = themes.sample 
    redirect_to root_path
  end
  def third_party_reader_fn
    content = File.open(File.join('/safe/dir', params[:file])).read
    render plain: content
  end
end