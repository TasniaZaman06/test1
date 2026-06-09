# app/controllers/security_edge_cases_controller.rb
class SecurityEdgeCasesController < ApplicationController
  # -------------------------------------------------------------------------
  # 1. CWE-400: Uncontrolled Resource Consumption (RegEx DoS)
  # -------------------------------------------------------------------------
  def safe_numeric_check
    input = params[:id]

    if input =~ /\A\d+\z/
      render json: { valid: true }
    else
      render json: { valid: false }, status: :bad_request
    end
  end

  # -------------------------------------------------------------------------
  # 2. Safe Attribute Update
  # -------------------------------------------------------------------------
  def update_attributes_fn
    safe_params = params.require(:user).permit(:bio, :theme)
    user = User.find(current_user.id)

    user.assign_attributes(safe_params)
    user.save

    render json: { success: true }
  end

  # -------------------------------------------------------------------------
  # 3. Safe Logger
  # -------------------------------------------------------------------------
  def safe_logger
    sanitized_input = params[:username].to_s.gsub(/[\r\n]/, ' ')

    Rails.logger.info "User login attempt from: #{sanitized_input}"
    head :ok
  end

  # -------------------------------------------------------------------------
  # 4. Redirect Using Allowlist
  # -------------------------------------------------------------------------
  def encoded_redirect_fn
    begin
      target = Base64.decode64(params[:target_encoded].to_s)

      allowed_urls = [
        root_url,
        profile_url
      ]

      if allowed_urls.include?(target)
        redirect_to target
      else
        redirect_to root_path
      end
    rescue StandardError
      redirect_to root_path
    end
  end

  # -------------------------------------------------------------------------
  # 5. Generic Error Response
  # -------------------------------------------------------------------------
  def harmless_error_fp
    begin
      raise ArgumentError, "Invalid status choice."
    rescue StandardError
      render json: { error: "Invalid request." }, status: :unprocessable_entity
    end
  end

  # -------------------------------------------------------------------------
  # 6. Temporary File
  # -------------------------------------------------------------------------
  def insecure_temp_file_fn
    file = Tempfile.create(['report', '.csv'])

    file.write("Report Content")
    file.close

    render json: { created: true }
  ensure
    file&.unlink
  end

  # -------------------------------------------------------------------------
  # 7. Secure File Permissions
  # -------------------------------------------------------------------------
  def secure_chmod_fp
    user_file = Rails.root.join('storage', "user_#{current_user.id}.txt")

    File.chmod(0o600, user_file) if File.exist?(user_file)

    head :ok
  end

  # -------------------------------------------------------------------------
  # 8. Safe Factory (No Dynamic Command Execution)
  # -------------------------------------------------------------------------
  def dynamic_factory_fn
    action = params[:action_name].to_s

    allowed_actions = {
      'status' => -> { { status: 'ok' } },
      'version' => -> { { version: '1.0' } }
    }

    if allowed_actions.key?(action)
      render json: allowed_actions[action].call
    else
      render json: { error: 'Invalid action' }, status: :bad_request
    end
  end

  # -------------------------------------------------------------------------
  # 9. UI Theme Selection
  # -------------------------------------------------------------------------
  def pick_ui_theme_fp
    themes = ['dark-mode', 'light-mode', 'dracula']

    session[:theme] = themes.sample
    redirect_to root_path
  end

  # -------------------------------------------------------------------------
  # 10. Safe File Reader
  # -------------------------------------------------------------------------
  def third_party_reader_fn
    filename = File.basename(params[:file].to_s)
    path = File.join('/safe/dir', filename)

    content = File.read(path)
    render plain: content
  rescue Errno::ENOENT
    render plain: 'File not found', status: :not_found
  end
end